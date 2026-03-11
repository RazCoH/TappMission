package com.example.tappmission.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle

@Composable
fun ErrorContent() {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(GlanceTheme.colors.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Vertical.CenterVertically
    ) {
        Column(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = "Error",
                style = TextStyle(
                    color = GlanceTheme.colors.error,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Failed to load data",
                style = TextStyle(color = GlanceTheme.colors.onSurface)
            )
            Spacer(modifier = GlanceModifier.height(8.dp))
        }
    }
}