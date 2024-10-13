package com.example.letsconnect.screens

import android.annotation.SuppressLint
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.letsconnect.MainActivity
import com.example.letsconnect.screens.components.User
import com.example.letsconnect.screens.components.appConstants
import com.example.letsconnect.ui.theme.Black
import com.example.letsconnect.viewmodels.MainScreenViewModel
import com.google.firebase.annotations.concurrent.Background
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import java.util.Collections

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val contentColor = if (isSystemInDarkTheme()) Black else Color.White
    val viewModel: MainScreenViewModel = hiltViewModel()
    val users by viewModel.loggedInUsers.collectAsState()
    val context = LocalContext.current as MainActivity

    LaunchedEffect(key1 = Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {

        context.initZegoCallService(appConstants.appId, appConstants.appSign, it.email!!, it.uid)
        }
    }

    Scaffold(
        containerColor = uiColor,
        contentColor = contentColor,
        topBar = {
            TopAppBar(
                title = {
                    Row {
                        Spacer(modifier = Modifier.width(50.dp))
                        Text(
                            text = "Let's Connect!",
                            color = uiColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = FontFamily.Serif,
                        )
                        Spacer(modifier = Modifier.width(40.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.Red
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
            )
        }, modifier = Modifier.background(uiColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(users) { user ->
                    UserItem(user = user) { isVideoCall ->
                        // Handle call button click
                        viewModel.initiateCall(user.email, isVideoCall)
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, onCallButtonClick: (Boolean) -> Unit) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    val contentColor = if (isSystemInDarkTheme()) Black else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(bottom = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = uiColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (user.email != FirebaseAuth.getInstance().currentUser?.email) user.email else "You",
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )
            Row (horizontalArrangement = Arrangement.SpaceBetween){
                Spacer(modifier = Modifier.width(8.dp))

                // Video call button
                AndroidView(factory = { context ->
                    ZegoSendCallInvitationButton(context).apply {
                        setIsVideoCall(true)
                        resourceID = "zego_uikit_call"
                        setInvitees(Collections.singletonList(ZegoUIKitUser(user.email)))
                        setOnClickListener(View.OnClickListener {
                            onCallButtonClick(true)
                        })
                    }
                })

                Spacer(modifier = Modifier.width(8.dp))

                // Voice call button
                AndroidView(factory = { context ->
                    ZegoSendCallInvitationButton(context).apply {
                        setIsVideoCall(false)
                        resourceID = "zego_uikit_call"
                        setInvitees(Collections.singletonList(ZegoUIKitUser(user.email)))
                        setOnClickListener(View.OnClickListener {
                            onCallButtonClick(false)
                        })
                    }
                })
            }
        }
    }
}


@Preview
@Composable
@Background
private fun view() {
    MainScreen(navController = NavController(LocalContext.current))
}

