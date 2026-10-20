package com.project.myshopeegw.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.project.myshopeegw.data.api.PaymentMethodProvider
import com.project.myshopeegw.data.model.CartItem
import com.project.myshopeegw.data.model.CheckoutPayload
import com.project.myshopeegw.data.model.Order
import com.project.myshopeegw.data.model.PaymentMethod
import com.project.myshopeegw.data.model.PaymentResponse
import com.project.myshopeegw.data.repository.CartRepository
import com.project.myshopeegw.data.repository.OrderRepository
import com.project.myshopeegw.utils.PrefManager
import com.project.myshopeegw.utils.Resource
import kotlinx.coroutines.launch

class CheckoutViewModel(application: Application) : AndroidViewModel(application) {

    private val cartRepository = CartRepository(application)
    private val orderRepository = OrderRepository(application)
    private val prefManager = PrefManager(application)

    private val _orderState = MutableLiveData<Resource<Order>>()
    val orderState: LiveData<Resource<Order>> = _orderState

    private val _paymentState = MutableLiveData<Resource<PaymentResponse>>()
    val paymentState: LiveData<Resource<PaymentResponse>> = _paymentState

    val shippingAddress = MutableLiveData(prefManager.userAddress)
    val shippingCity = MutableLiveData("Kota Cirebon")
    val shippingType = MutableLiveData("Reguler (JNE Cirebon)")
    val shippingCost = MutableLiveData(15000L)

    val checkoutItems = MutableLiveData<List<CartItem>>()
    val subtotal = MutableLiveData(0L)
    val totalAmount = MutableLiveData(0L)

    val selectedPaymentMethod = MutableLiveData<PaymentMethod>(PaymentMethodProvider.getDefault())

    fun prepareCheckout(items: List<CartItem>) {
        val selected = if (items.isNotEmpty()) items else cartRepository.clearSelectedItemsSync()
        checkoutItems.value = selected
        val sub = selected.sumOf { it.price * it.quantity }
        subtotal.value = sub
        recalculateTotal()
    }

    fun setPaymentMethod(method: PaymentMethod) {
        selectedPaymentMethod.value = method
    }

    fun setShippingOption(type: String, cost: Long) {
        shippingType.value = type
        shippingCost.value = cost
        recalculateTotal()
    }

    private fun recalculateTotal() {
        val sub = subtotal.value ?: 0L
        val ship = shippingCost.value ?: 15000L
        totalAmount.value = sub + ship
    }

    fun submitOrder() {
        val items = checkoutItems.value ?: emptyList()
        val method = selectedPaymentMethod.value ?: PaymentMethodProvider.getDefault()

        val payload = CheckoutPayload(
            items = items,
            shippingAddress = shippingAddress.value ?: prefManager.userAddress,
            shippingCity = shippingCity.value ?: "Kota Cirebon",
            shippingType = shippingType.value ?: "Reguler (JNE Cirebon)",
            shippingCost = shippingCost.value ?: 15000L,
            subtotal = subtotal.value ?: 0L,
            totalAmount = totalAmount.value ?: 0L,
            paymentMethod = method.name,
            vaNumber = method.codeOrVa
        )

        _orderState.value = Resource.Loading()
        viewModelScope.launch {
            val result = orderRepository.createOrder(payload)
            _orderState.value = result
            if (result is Resource.Success) {
                // Remove ordered items from Cart both locally and in Supabase
                cartRepository.removeItems(items)
            }
        }
    }

    fun simulatePayment(orderId: String, items: List<CartItem> = emptyList()) {
        _paymentState.value = Resource.Loading()
        viewModelScope.launch {
            val result = orderRepository.simulatePayment(orderId)
            _paymentState.value = result
            if (result is Resource.Success) {
                // Ensure items are cleared from cart
                if (items.isNotEmpty()) {
                    cartRepository.removeItems(items)
                } else {
                    val order = orderRepository.getLastOrder()
                    if (order != null && order.items.isNotEmpty()) {
                        cartRepository.removeItems(order.items)
                    }
                }
            }
        }
    }

    fun getLastOrder(): Order? {
        return orderRepository.getLastOrder()
    }
}
