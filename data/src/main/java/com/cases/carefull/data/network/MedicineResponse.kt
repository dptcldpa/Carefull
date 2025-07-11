//package com.cases.carefull.data.network
//
//import com.squareup.moshi.Json
//import com.squareup.moshi.JsonClass
//
//@JsonClass(generateAdapter = true)
//data class MedicineResponse(
//    @Json(name = "response") val response: ResponseContent?
//)
//
//@JsonClass(generateAdapter = true)
//data class ResponseContent(
//    @Json(name = "header") val header: Header?,
//    @Json(name = "body") val body: MedicineBody?
//)
//
//@JsonClass(generateAdapter = true)
//data class Header(
//    @Json(name = "resultCode") val resultCode: String?,
//    @Json(name = "resultMsg") val resultMsg: String?
//)
//
//@JsonClass(generateAdapter = true)
//data class MedicineBody(
//    @Json(name = "items") val items: List<MedicineItem>?
//)
//
//@JsonClass(generateAdapter = true)
//data class MedicineItem(
//    @Json(name = "entpName") val entpName: String?,
//    @Json(name = "itemName") val itemName: String?,
//    @Json(name = "efcyQesitm") val efficacy: String?,
//    @Json(name = "useMethodQesitm") val usage: String?,
//    @Json(name = "atpnWarnQesitm") val warning: String?,
//    @Json(name = "intrcQesitm") val interaction: String?,
//    @Json(name = "seQesitm") val sideEffect: String?,
//    @Json(name = "depositMethodQesitm") val storage: String?
//)