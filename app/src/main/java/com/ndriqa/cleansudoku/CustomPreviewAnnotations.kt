package com.ndriqa.cleansudoku

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "dark - portrait",
    device = "spec:width=400dp,height=800dp,dpi=480",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "light - portrait",
    device = "spec:width=400dp,height=800dp,dpi=480",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "dark - landscape",
    device = "spec:width=800dp,height=400dp,dpi=480",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "light - landscape",
    device = "spec:width=800dp,height=400dp,dpi=480",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
annotation class FullPreviews
