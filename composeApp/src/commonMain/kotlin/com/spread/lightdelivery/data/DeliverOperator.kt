package com.spread.lightdelivery.data

import com.spread.lightdelivery.INVALID_DATE
import com.spread.lightdelivery.YMDStr
import com.spread.lightdelivery.toDate
import org.apache.poi.ss.usermodel.BorderStyle
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.Date

object DeliverOperator {

    val sheets: List<DeliverSheet>
        get() {
            val list = arrayListOf<DeliverSheet>()
            val dir = File("delivery")
            if (!dir.exists() || !dir.isDirectory) {
                return list
            }
            val files = dir.listFiles() ?: return list
            for (file in files) {
                if (file.isDirectory) {
                    continue
                }
                if (file.extension != "xlsx") {
                    continue
                }
                val sheet = readFromFile(file.absolutePath)
                if (sheet.isNotEmpty()) {
                    list.addAll(sheet)
                }
            }
            return list
        }

    fun deleteSheet(sheet: DeliverSheet) {
        val file = File("delivery/${sheet.fileName}")
        if (file.exists()) {
            file.delete()
        }
    }

    fun fileExists(fileName: String): Boolean {
        return File("delivery/$fileName").exists()
    }

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
                        val cell = row.getCell(1)
                        if (cell != null) {
                            val cellType = cell.cellType
                            when (cellType) {
                                CellType.STRING -> {
                                    val str = cell.stringCellValue
                                    date = try {
                                        str.toDate()
                                    } catch (e: Exception) {
                                        null
                                    }
                                }

                                else -> {
                                    date = try {
                                        cell.dateCellValue
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                            }
                        }
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
                        customerName,
                        deliverAddress,
                        date ?: INVALID_DATE,
                        deliverItems
                    ).apply { fromLocal = true }
                )
            }
        }
        return sheetList
    }

    fun writeToFile(fileName: String, sheet: DeliverSheet, isNew: Boolean): Boolean {
        val file = File("delivery/$fileName")
        if (!file.exists()) {
            file.createNewFile()
        } else if (isNew) {
            // 文件已经存在，还要创建新表格，则直接保存失败
            return false
        }
        val workbook = XSSFWorkbook()
        val xlsSheet = workbook.createSheet("Sheet1")
        val style = workbook.createCellStyle().apply {
            setFont(workbook.createFont().apply {
                fontName = "宋体"
                fontHeightInPoints = 16
                bold = true
            })
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            borderTop = BorderStyle.THIN
            borderBottom = BorderStyle.THIN
            borderLeft = BorderStyle.THIN
            borderRight = BorderStyle.THIN
        }

        // row 0 title
        val rowTitle = xlsSheet.createRow(0)
        rowTitle.createCell(1).cellStyle = style
        rowTitle.createCell(2).cellStyle = style
        rowTitle.createCell(3).cellStyle = style
        val cellTitle = rowTitle.createCell(0)
        cellTitle.setCellValue(sheet.title)
        cellTitle.cellStyle = style
        xlsSheet.addMergedRegion(CellRangeAddress(0, 0, 0, 3))

        // row 1 customer
        val rowCustomer = xlsSheet.createRow(1)
        rowCustomer.createCell(0).setCellValue("客户名称")
        rowCustomer.createCell(1).cellStyle = style
        rowCustomer.createCell(2).cellStyle = style
        rowCustomer.createCell(3).cellStyle = style
        val cellCustomer = rowCustomer.createCell(1)
        cellCustomer.setCellValue(sheet.customerName)
        xlsSheet.addMergedRegion(CellRangeAddress(1, 1, 1, 3))
        rowCustomer.getCell(0).cellStyle = style
        cellCustomer.cellStyle = style

        // row 2 address
        val rowAddress = xlsSheet.createRow(2)
        rowAddress.createCell(1).cellStyle = style
        rowAddress.createCell(2).cellStyle = style
        rowAddress.createCell(3).cellStyle = style
        rowAddress.createCell(0).setCellValue("送货地址")
        val cellAddress = rowAddress.createCell(1)
        cellAddress.setCellValue(sheet.deliverAddress)
        xlsSheet.addMergedRegion(CellRangeAddress(2, 2, 1, 3))
        rowAddress.getCell(0).cellStyle = style
        cellAddress.cellStyle = style

        // row 3 date
        val rowDate = xlsSheet.createRow(3)
        rowDate.createCell(1).cellStyle = style
        rowDate.createCell(2).cellStyle = style
        rowDate.createCell(3).cellStyle = style
        rowDate.createCell(0).setCellValue("送货日期")
        val cellDate = rowDate.createCell(1)
        cellDate.setCellValue(sheet.date.YMDStr)
        xlsSheet.addMergedRegion(CellRangeAddress(3, 3, 1, 3))
        rowDate.getCell(0).cellStyle = style
        cellDate.cellStyle = style

        // row 4 item headers
        val rowItemHeaders = xlsSheet.createRow(4)
        val headers = arrayOf("产品名称", "数量", "价格", "合计")
        for (i in headers.indices) {
            val cell = rowItemHeaders.createCell(i)
            cell.setCellValue(headers[i])
            cell.cellStyle = style
        }

        val columnWidths = arrayOf(17, 13, 13, 17)
        for (i in headers.indices) {
            xlsSheet.setColumnWidth(i, columnWidths[i] * 256)
        }
        rowTitle.heightInPoints = 45f       // 第1行
        rowCustomer.heightInPoints = 36f    // 第2行
        rowAddress.heightInPoints = 36f     // 第3行
        rowDate.heightInPoints = 36f        // 第4行
        rowItemHeaders.heightInPoints = 36f // 第5行

        // items
        for ((index, item) in sheet.deliverItems.withIndex()) {
            val rowItem = xlsSheet.createRow(index + 5)
            rowItem.createCell(0).apply {
                setCellValue(item.name)
                cellStyle = style
            }
            rowItem.createCell(1).apply {
                setCellValue(item.count.toDouble())
                cellStyle = style
            }
            rowItem.createCell(2).apply {
                setCellValue(item.price)
                cellStyle = style
            }
            rowItem.createCell(3).apply {
                setCellValue(item.totalPrice)
                cellStyle = style
            }
            rowItem.heightInPoints = 27f
        }

        val rowTotal = xlsSheet.createRow(sheet.deliverItems.size + 5)
        rowTotal.createCell(0).apply {
            setCellValue("总计金额")
            cellStyle = style
            rowTotal.heightInPoints = 27f
        }
        rowTotal.createCell(1).cellStyle = style
        rowTotal.createCell(2).cellStyle = style
        rowTotal.createCell(3).apply {
            setCellValue(sheet.totalPrice)
            cellStyle = style
            rowTotal.heightInPoints = 27f
        }

        FileOutputStream(file).use {
            workbook.write(it)
            sheet.fromLocal = true
            return true
        }
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