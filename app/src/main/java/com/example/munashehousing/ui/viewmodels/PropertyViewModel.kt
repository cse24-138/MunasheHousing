package com.example.munashehousing.ui.viewmodels

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.munashehousing.R
import com.example.munashehousing.data.PropertyRepository
import com.example.munashehousing.data.database.*
import com.example.munashehousing.models.Property
import com.example.munashehousing.models.UserRole
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

class PropertyViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PropertyRepository
    private val db: AppDatabase
    
    private val _searchQuery = MutableStateFlow("")
    private val _selectedCategory = MutableStateFlow("All")
    private val _minPrice = MutableStateFlow(0)
    private val _maxPrice = MutableStateFlow(100000)
    
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        db = AppDatabase.getDatabase(application)
        repository = PropertyRepository(db)
        createNotificationChannel()
    }

    val properties: StateFlow<List<Property>> = combine(
        repository.allProperties, _searchQuery, _selectedCategory, _minPrice, _maxPrice
    ) { allProps, query, category, min, max ->
        allProps.filter {
            (it.location.contains(query, ignoreCase = true) || it.title.contains(query, ignoreCase = true)) &&
            (category == "All" || it.type == category) &&
            (it.price in min..max)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val userReservations: StateFlow<List<ReservationEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) repository.db.reservationDao().getReservationsForUser(user.id)
        else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearch(query: String) { _searchQuery.value = query }
    fun updateCategory(cat: String) { _selectedCategory.value = cat }
    fun updatePriceRange(min: Int, max: Int) {
        _minPrice.value = min
        _maxPrice.value = max
    }

    fun login(email: String, onResult: (UserEntity?) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginUser(email)
            _currentUser.value = user
            onResult(user)
        }
    }

    fun registerUser(name: String, email: String, phone: String, role: UserRole, nationalId: String, guardianName: String) {
        viewModelScope.launch {
            val user = UserEntity(UUID.randomUUID().toString(), name, email, phone, role.name, nationalId, guardianName)
            repository.registerUser(user)
            _currentUser.value = user
        }
    }

    fun reserveProperty(property: Property) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val ref = "REF-PAY-${(100000..999999).random()}"
            val reservation = ReservationEntity(ref, property.id, user.id, property.deposit)
            repository.addReservation(reservation)
            triggerAlert("Payment Successful", "Receipt: $ref. House ${property.title} is now reserved.")
        }
    }

    fun addNewProperty(property: Property) {
        viewModelScope.launch {
            repository.insertProperty(property)
            val prefs = repository.getPreferences(_currentUser.value?.id ?: "")
            if (prefs != null && property.price in prefs.minPrice..prefs.maxPrice) {
                triggerAlert("Match Found!", "A new ${property.type} matches your preferences!")
            }
        }
    }

    fun getChatHistory(receiver: String): Flow<List<MessageEntity>> {
        return db.messageDao().getChatHistory(_currentUser.value?.name ?: "User", receiver)
    }

    fun sendMessage(receiverName: String, text: String) {
        viewModelScope.launch {
            val message = MessageEntity(senderId = _currentUser.value?.name ?: "User", receiverName = receiverName, text = text)
            db.messageDao().insertMessage(message)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("HOUSING_ALERTS", "Housing Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun triggerAlert(title: String, message: String) {
        val builder = NotificationCompat.Builder(getApplication(), "HOUSING_ALERTS")
            .setSmallIcon(R.drawable.ic_launcher_foreground).setContentTitle(title).setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT).setAutoCancel(true)
        val manager = getApplication<Application>().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
