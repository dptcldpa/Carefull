package com.cases.carefull.domain.model.exercise

import kotlin.math.abs
import kotlin.math.atan2

// 신뢰도 임계값. 이 값보다 낮으면 부정확한 랜드마크로 간주합니다. (0.0 ~ 1.0)
// 실제 앱에서는 이 값을 조정하며 최적의 값을 찾아야 합니다.
const val MIN_CONFIDENCE = 0.7f

/**
 * 세 개의 랜드마크를 사용하여 두 벡터 사이의 각도를 계산합니다.
 * 각도는 midPoint를 꼭짓점으로 하여 형성됩니다.
 *
 * @param firstPoint 첫 번째 점 (예: 어깨)
 * @param midPoint 중간 점, 각도의 꼭짓점 (예: 팔꿈치)
 * @param lastPoint 마지막 점 (예: 손목)
 * @return 0.0에서 180.0 사이의 각도(degree) 값. 랜드마크가 유효하지 않으면 0.0을 반환합니다.
 */
fun getAngle(firstPoint: Landmark?, midPoint: Landmark?, lastPoint: Landmark?): Double {
	// 1. 입력 값 유효성 검사
	// 랜드마크 중 하나라도 null이거나, 신뢰도(inFrameLikelihood)가 임계값보다 낮으면
	// 유효하지 않은 각도로 판단하고 계산을 중단합니다.
	if (firstPoint == null || midPoint == null || lastPoint == null ||
		firstPoint.inFrameLikelihood < MIN_CONFIDENCE ||
		midPoint.inFrameLikelihood < MIN_CONFIDENCE ||
		lastPoint.inFrameLikelihood < MIN_CONFIDENCE
	) {
		return 0.0
	}
	
	// 2. 각도 계산
	// atan2(y, x) 함수는 원점과 (x, y) 점을 잇는 선이 x축의 양의 방향과 이루는 각도를 라디안 단위로 반환합니다.
	// 이를 이용하여 두 벡터(midPoint->lastPoint, midPoint->firstPoint)가 x축과 이루는 각을 각각 구한 뒤,
	// 그 차이를 통해 두 벡터 사이의 각도를 얻습니다.
	val angleRad = atan2(
		lastPoint.position.y - midPoint.position.y,
		lastPoint.position.x - midPoint.position.x
	) - atan2(
		firstPoint.position.y - midPoint.position.y,
		firstPoint.position.x - midPoint.position.x
	)
	
	// 3. 라디안(radian)을 각도(degree)로 변환합니다.
	// 자바의 Math 라이브러리를 사용합니다.
	var angleDeg = Math.toDegrees(angleRad.toDouble())
	
	// 4. 각도를 0 ~ 180 범위로 정규화합니다.
	// 계산된 각도는 음수이거나 180도를 초과(외각)할 수 있습니다.
	// 관절 각도는 보통 0~180도 사이의 양수인 내각으로 표현하므로 정규화 과정이 필요합니다.
	angleDeg = abs(angleDeg) // 절대값으로 변환하여 항상 양수가 되도록 합니다.
	if (angleDeg > 180) {
		angleDeg = 360 - angleDeg // 180도를 초과하면 외각이므로, 360에서 빼서 내각을 구합니다.
	}
	
	return angleDeg
}