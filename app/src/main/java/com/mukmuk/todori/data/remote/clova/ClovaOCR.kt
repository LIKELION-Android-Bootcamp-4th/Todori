package com.mukmuk.todori.data.remote.clova

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// CLOVA OCR 요청 바디
data class ClovaOcrRequest(
    val version: String = "V2", // 고정값
    val requestId: String = System.currentTimeMillis().toString(), // 고유한 요청 ID
    val timestamp: Long = System.currentTimeMillis(), // 타임스탬프
    val lang: String = "ko", // 언어 설정 (ko, en, ja, zh-Hans, zh-Hant)
    val images: List<ClovaOcrImage> // 이미지 정보 리스트
)

data class ClovaOcrImage(
    val format: String, // 이미지 형식 (jpg, png, pdf, tiff 등)
    val name: String, // 이미지 이름 (아무거나)
    val data: String? = null, // Base64 인코딩된 이미지 데이터 (파일 업로드 방식 시 사용)
    val url: String? = null, // 이미지 URL (URL 방식 시 사용)
    val inferText: Boolean = true // 텍스트 추출 여부 (기본값 true)
)

// CLOVA OCR 응답 바디
data class ClovaOcrResponse(
    val version: String,
    val requestId: String,
    val timestamp: Long,
    val images: List<ClovaOcrResultImage>
)

data class ClovaOcrResultImage(
    val uid: String,
    val name: String,
    val inferResult: String, // "SUCCESS" 또는 "FAIL"
    val message: String,
    val validationResult: ClovaOcrValidationResult?,
    val fields: List<ClovaOcrField>,
    val title: ClovaOcrTitle?,
    val conversionInfo: ClovaOcrConversionInfo?
)

data class ClovaOcrValidationResult(
    val result: String
)

data class ClovaOcrField(
    val name: String,
    val inferText: String, // 인식된 텍스트
    val inferConfidence: Double, // 인식 정확도 (0.0 ~ 1.0)
    val boundingPoly: ClovaOcrBoundingPoly,
    val lineBreak: Boolean, // 줄바꿈 여부
    val textStyle: ClovaOcrTextStyle?
)

data class ClovaOcrBoundingPoly(
    val vertices: List<ClovaOcrVertex>
)

data class ClovaOcrVertex(
    val x: Double,
    val y: Double
)

data class ClovaOcrTextStyle(
    val fontFamilly: String,
    val fontSize: Double,
    val textDirection: String
)

data class ClovaOcrTitle(
    val name: String,
    val inferText: String,
    val boundingPoly: ClovaOcrBoundingPoly
)

data class ClovaOcrConversionInfo(
    val pageIndex: Int,
    val totalPage: Int
)

// Retrofit 서비스 인터페이스
interface ClovaOcrService {
    @Headers("Content-Type: application/json")
    @POST(".") // Base URL에 Post 요청
    suspend fun recognizeText(
        @Header("X-OCR-SECRET") secretKey: String,
        @Body request: ClovaOcrRequest
    ): Response<ClovaOcrResponse>
}