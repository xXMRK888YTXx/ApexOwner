package com.xxmrk888ytxx.apexowner.view.mainActivity.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xxmrk888ytxx.apexowner.view.mainActivity.model.ApexOwnerBottomBarItem
import com.xxmrk888ytxx.core.compose.asString

@Composable
fun ApexOwnerBottomBar(
    items: List<ApexOwnerBottomBarItem>,
    selectedItemIndex: Int,
    onItemClicked: (ApexOwnerBottomBarItem) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        shadowElevation = 8.dp
    ) {
        NavigationBar(
            tonalElevation = 0.dp
        ) {
            items.forEach { item ->
                NavigationBarItem(
                    selected = selectedItemIndex == item.id,
                    onClick = { onItemClicked(item) },
                    icon = {
                        Icon(
                            painter = painterResource(id = item.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(text = item.text.asString())
                    },
                    colors = NavigationBarItemDefaults.colors()
                )
            }
        }
    }
}