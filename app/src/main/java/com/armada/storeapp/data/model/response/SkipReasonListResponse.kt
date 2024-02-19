package com.armada.storeapp.data.model.response

data class SkipReasonListResponse(
    val businessDate: String?,
    val displayMessage: Any?,
    val documentNo: Any?,
    val exceptionMessage: Any?,
    val iDs: Any?,
    val recordCount: Int?,
    val responseDynamicData: Any?,
    val skipReasonsList: List<SkipReasons?>?,
    val stackTrace: Any?,
    val statusCode: Int?,
    val wmsiDs: Any?
) {
    data class SkipReasons(
        val active: Boolean?,
        val appVersion: Any?,
        val createBy: Any?,
        val createOn: Any?,
        val createdByUserName: Any?,
        val id: Int?,
        val isCountrySync: Boolean?,
        val isServerSync: Boolean?,
        val isStoreSync: Boolean?,
        val scn: Any?,
        val skipReasonCode: String?,
        val skipReasonName: String?,
        val updateBy: Any?,
        val updateOn: Any?,
        val updatedByUserName: Any?
    )
}