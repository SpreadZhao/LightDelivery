package com.spread.lightdelivery.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
class Config private constructor() {

    companion object {

        private const val FILE_NAME = "config.json"

        private lateinit var config: Config

        private val json = Json {
            ignoreUnknownKeys = true
            isLenient = true
            prettyPrint = true
        }

        fun get(): Config {
            if (::config.isInitialized) {
                return config
            }
            val file = File(FILE_NAME)
            if (!file.exists()) {
                file.createNewFile()
                config = Config()
                return config
            }
            return try {
                config = json.decodeFromString(file.readText())
                config
            } catch (t: Throwable) {
                config = Config()
                config
            }
        }

        fun updateCustomers(customers: List<Customer>) {
            if (!::config.isInitialized) {
                get()
            }
            config._customers = customers.filter { it.name.isNotBlank() }.toMutableList()
            config.saveToFile()
        }

        fun updateItems(items: List<Item>) {
            if (!::config.isInitialized) {
                get()
            }
            config._items = items.filter { it.name.isNotBlank() }.toMutableList()
            config.saveToFile()
        }

        fun updateWholesaler(name: String) {
            if (!::config.isInitialized) {
                get()
            }
            config.updateWholesaler(name)
        }

        fun addNewItem(item: Item) {
            if (!::config.isInitialized) {
                get()
            }
            config.addNewItem(item)
        }

        fun addNewCustomer(customer: Customer) {
            if (!::config.isInitialized) {
                get()
            }
            config.addNewCustomer(customer)
        }
    }

    @Serializable
    data class Customer(
        @SerialName("name") var name: String,
        @SerialName("address") var address: String
    )

    @Serializable
    data class Item(
        @SerialName("name") var name: String
    )

    @SerialName("wholesaler")
    private var _wholesaler: String? = null

    @SerialName("customers")
    private var _customers: MutableList<Customer>? = null

    @SerialName("items")
    private var _items: MutableList<Item>? = null

    val wholesaler: String?
        get() = _wholesaler

    val customers: List<Customer>?
        get() = _customers

    val items: List<Item>?
        get() = _items

    fun updateWholesaler(name: String) {
        _wholesaler = name
        saveToFile()
    }

    fun addNewItem(item: Item) {
        if (_items == null) {
            _items = mutableListOf()
        }
        _items?.run {
            if (find { it.name == item.name } == null) {
                add(item)
                saveToFile()
            }
        }
    }

    fun addNewCustomer(customer: Customer) {
        if (_customers == null) {
            _customers = mutableListOf()
        }
        _customers?.run {
            val target = find { it.name == customer.name }
            if (target == null) {
                add(customer)
            } else {
                remove(target)
                add(customer)
            }
            saveToFile()
        }
    }

    fun saveToFile(): Boolean {
        try {
            val string = json.encodeToString(this)
            val file = File(FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
            file.createNewFile()
            file.writeText(string)
            return true
        } catch (t: Throwable) {
            return false
        }
    }

}