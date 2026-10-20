package com.example.iiushka

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iiushka.presentation.WelcomeNavigationEvent
import com.example.iiushka.presentation.WelcomeViewModel
import com.example.iiushka.presentation.createWelcomeViewModel
import com.example.iiushka.ui.theme.AppColors

@Composable
fun WelcomeScreen(
    onNavigateToChat: () -> Unit,
    vm: WelcomeViewModel = viewModel { createWelcomeViewModel() },
) {
    LaunchedEffect(Unit) {
        vm.navigationEvents.collect { event ->
            when (event) {
                WelcomeNavigationEvent.NavigateToChat -> onNavigateToChat()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppColors.Background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier.size(96.dp),
            )

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(R.string.chat_title),
                color = AppColors.TextPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(Modifier.height(8.dp))

            Text(
                stringResource(R.string.welcome_tagline),
                color = AppColors.TextSecondary,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(48.dp))

            Button(
                onClick = { vm.onStartClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.TextPrimary,
                    contentColor = AppColors.Background,
                ),
            ) {
                Text(
                    stringResource(R.string.welcome_action),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(R.string.welcome_description),
                color = AppColors.TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}
