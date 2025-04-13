package com.spread.lightdelivery.data

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.util.Date

object DeliverOperator {

    fun readFromFile(path: String): List<DeliverSheet> {
        val sheetList = mutableListOf<DeliverSheet>()
        val file = File(path)
        if (!file.exists()) {
            println("file not exist: $path")
            return sheetList
        }
        FileInputStream(file).use { fis ->
            val workbook = XSSFWorkbook(fis)
            for (i in 0 until workbook.numberOfSheets) {
                val sheet = workbook.getSheetAt(i) ?: continue
                var title = ""
                var phoneNumber = ""
                var customerName = ""
                var deliverAddress = ""
                var date: Date? = null
                val deliverItems = mutableListOf<DeliverItem>()
                for ((index, row) in sheet.withIndex()) {
                    if (index == 0) {
                        row.getCell(0)?.stringCellValue?.let { t ->
                            title = t
                            t.extractPhoneNumber()?.let { num -> phoneNumber = num }
                        }
                        continue
                    }
                    if (index == 1) {
                        row.getCell(1)?.stringCellValue?.let { customerName = it }
                        continue
                    }
                    if (index == 2) {
                        row.getCell(1)?.stringCellValue?.let { deliverAddress = it }
                        continue
                    }
                    if (index == 3) {
                        row.getCell(1)?.dateCellValue?.let { date = it }
                    }
                    if (index > 4) {
                        if (checkSheetEnd(row)) {
                            break
                        }
                        val name = row.getCell(0)?.stringCellValue ?: ""
                        val count = row.getCell(1)?.numericCellValue?.toInt() ?: -1
                        val price = row.getCell(2)?.numericCellValue ?: -1.0
                        val totalPrice = row.getCell(3)?.numericCellValue ?: -1.0
                        val deliverItem = DeliverItem(name, count, price)
                        deliverItem.checkValid(totalPrice)
                        deliverItems.add(deliverItem)
                    }
                }
                sheetList.add(
                    DeliverSheet(
                        title,
                        phoneNumber,
                        customerName,
                        deliverAddress,
                        date ?: Date(0),
                        deliverItems
                    )
                )
            }
        }
        return sheetList
    }


    private fun checkSheetEnd(row: Row): Boolean {
        if (row.getCell(0)?.stringCellValue == "总计金额") {
            return true
        }
        try {
            if (row.getCell(1).numericCellValue < 0) {
                return true
            }
        } catch (e: IllegalStateException) {
            return true
        } catch (e: NumberFormatException) {
            return true
        }
        return false
    }

    private fun String.extractPhoneNumber() = Regex("\\d+").find(this)?.value

    private fun DeliverItem.checkValid(totalPrice: Double) {
        val p = this.totalPrice
        if (p < 0.0 || totalPrice < 0.0 || p != totalPrice) {
            this.errMsg = "价格校验错误。表格中记录的值：${totalPrice}，计算值：${this.totalPrice}"
        }
    }

}