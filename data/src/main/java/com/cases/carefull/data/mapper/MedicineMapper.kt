package com.cases.carefull.data.mapper

import com.cases.carefull.data.dto.MedicineItemDto
import com.cases.carefull.domain.model.MedicineItem

fun MedicineItemDto.toDomain(): MedicineItem {
    return MedicineItem(
        entpName = this.entpName,
        itemName = this.itemName,
        efcyQesitm = this.efcyQesitm,
        useMethodQesitm = this.useMethodQesitm,
        atpnWarnQesitm = this.atpnWarnQesitm,
        atpnQesitm = this.atpnQesitm,
        intrcQesitm = this.intrcQesitm,
        seQesitm = this.seQesitm,
        depositMethodQesitm = this.depositMethodQesitm,
        itemImage = this.itemImage
    )
}
