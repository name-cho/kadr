package eu.kanade.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Corner scale used across Kedr Mihon.
 *
 * Material 3 Expressive widens the corner scale and encourages pairing a large outer
 * radius with a smaller inner one. The five [Shapes] slots are mapped to that scale:
 *
 * - extraSmall  -> 8dp  (badges, tiny chips)
 * - small       -> 12dp (inputs, compact controls)
 * - medium      -> 16dp (list rows, small cards)
 * - large       -> 24dp (content cards, menus)
 * - extraLarge  -> 32dp (sheets, hero containers, floating navigation)
 */
val ExpressiveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

/**
 * Typography with a heavier, tighter display/headline scale.
 *
 * Body styles stay at the Material baselines for readability; the expressive part of the
 * scale carries the hierarchy. Emphasized styles are selected per component instead of
 * replacing the whole scale.
 */
val ExpressiveTypography: Typography = Typography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-1).sp,
        ),
        displayMedium = base.displayMedium.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.5).sp,
        ),
        displaySmall = base.displaySmall.copy(fontWeight = FontWeight.SemiBold),
        headlineLarge = base.headlineLarge.copy(
            fontWeight = FontWeight.SemiBold,
            letterSpacing = (-0.25).sp,
        ),
        headlineMedium = base.headlineMedium.copy(fontWeight = FontWeight.SemiBold),
        headlineSmall = base.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
        titleLarge = base.titleLarge.copy(fontWeight = FontWeight.SemiBold),
        titleMedium = base.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        titleSmall = base.titleSmall.copy(fontWeight = FontWeight.SemiBold),
        labelLarge = base.labelLarge.copy(fontWeight = FontWeight.Medium),
    )
}
