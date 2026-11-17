package com.montanainc.simpleloginscreen

import com.google.firebase.firestore.FirebaseFirestore
import com.montanainc.simpleloginscreen.entities.Discount
import kotlin.math.ceil
import kotlin.math.roundToLong

const val DAY_MS = 86_400_000L

const val LATE_FEE_MULTIPLIER = 1.5

fun roundMoney(value: Double): Double = (value * 100).roundToLong() / 100.0

fun money(value: Double): String = "\$%.2f".format(value)

fun calculateLateFee(endDate: Long, pricePerDay: Double, now: Long = System.currentTimeMillis()): Double {
    if (now <= endDate) return 0.0
    val daysLate = ceil((now - endDate) / DAY_MS.toDouble()).toInt()
    return roundMoney(daysLate * pricePerDay * LATE_FEE_MULTIPLIER)
}

fun validateDiscountCode(discount: Discount?): String? = when {
    discount == null -> "Invalid or inactive code"
    discount.amount <= 0.0 -> "This code has no value"
    else -> null
}

fun applyFixedDiscount(subtotal: Double, discount: Discount?): Double {
    if (validateDiscountCode(discount) != null) return subtotal
    return roundMoney((subtotal - discount!!.amount).coerceAtLeast(0.0))
}

fun sendEmail(to: String, subject: String, text: String) {
    if (to.isBlank()) return
    val payload = hashMapOf(
        "to" to listOf(to),
        "message" to hashMapOf("subject" to subject, "text" to text)
    )
    FirebaseFirestore.getInstance().collection("mail").add(payload)
}
