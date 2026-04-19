package dev.alejandrorosas.core.ui

import android.content.res.Configuration
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest

@PreviewTest
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ErrorViewRateLimited() {
    AppTheme {
        Surface {
            ErrorView(message = "You've exceeded your rate limit. Please wait some time and try again.")
        }
    }
}

@PreviewTest
@Preview(showBackground = true)
@Composable
private fun ErrorViewShortMessage() {
    AppTheme {
        Surface {
            ErrorView(message = "Network error")
        }
    }
}
