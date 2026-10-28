package tachiyomi.presentation.core.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.icerock.moko.resources.StringResource
import tachiyomi.presentation.core.components.material.padding
import tachiyomi.presentation.core.i18n.stringResource

@Composable
fun LazyItemScope.SectionCard(
    titleRes: StringResource? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (titleRes != null) {
        Text(
            modifier = Modifier.padding(
                start = MaterialTheme.padding.medium,
                end = MaterialTheme.padding.medium,
                top = MaterialTheme.padding.large,
                bottom = MaterialTheme.padding.small,
            ),
            text = stringResource(titleRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelLarge,
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = MaterialTheme.padding.medium),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(modifier = Modifier.padding(MaterialTheme.padding.medium)) {
            content()
        }
    }
}
