package com.example.letsconnect.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.letsconnect.R
import com.example.letsconnect.screens.components.TextFieldView
import com.example.letsconnect.ui.theme.Black
import com.example.letsconnect.ui.theme.BlueGray
import com.example.letsconnect.viewmodels.SignUpState
import com.example.letsconnect.viewmodels.SignUpViewModel

@Composable
fun SignUpScreen(navController: NavController) {
    val viewModel: SignUpViewModel = hiltViewModel()
    val loading = remember { mutableStateOf(false) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    val state = viewModel.signUpStateFlow.collectAsState()
    LaunchedEffect(state.value) {
        when (state.value) {
            is SignUpState.Loading -> {
                loading.value = true
            }
            is SignUpState.Success -> {
                loading.value = false
                navController.navigate("home") {
                    popUpTo(0) {
                        saveState = true
                    }
                    launchSingleTop = true
                }
            }
            is SignUpState.Error -> {
                loading.value = false
                errorMessage.value = (state.value as SignUpState.Error).message
            }
            is SignUpState.Normal -> {
                loading.value = false
            }
        }
    }

    Surface {
        Column(modifier = Modifier.fillMaxSize()) {
            SignUpTopSection()
            Spacer(modifier = Modifier.height(36.dp))


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp)
                ) {
                    SignUpSection(viewModel, errorMessage.value)
                    Spacer(modifier = Modifier.height(20.dp))
                    SignUpBottomSection(onClick = {
                        navController.navigate("login") {
                            popUpTo(0) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    })
                }
            }
        }
    }


@Composable
private fun SignUpSection(viewModel: SignUpViewModel, errorMessage: String?) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    var confirmPasswordVisibility by remember { mutableStateOf(false) }


    TextFieldView(
        modifier = Modifier.fillMaxWidth(),
        label = "Email",
        value = email,
        trailing = null,
        onValueChange = { email = it },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
    Spacer(modifier = Modifier.height(15.dp))
    TextFieldView(
        modifier = Modifier.fillMaxWidth(),
        label = "Password",
        value = password,
        trailing =  {
            IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                Icon(
                    painter = painterResource(id = if (passwordVisibility) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                    contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                )
            }
        },
        onValueChange = { password = it },
        isPassword = !passwordVisibility,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next
        )
    )
    Spacer(modifier = Modifier.height(15.dp))
    TextFieldView(
        modifier = Modifier.fillMaxWidth(),
        label = "Confirm Password",
        value = confirmPassword,
        trailing =  {
            IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                Icon(
                    painter = painterResource(id = if (confirmPasswordVisibility) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                    contentDescription = if (confirmPasswordVisibility) "Hide password" else "Show password"
                )
            }
        },
        onValueChange = { confirmPassword = it },
        isPassword = !confirmPasswordVisibility,
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        )
    )
    Spacer(modifier = Modifier.height(15.dp))

    // Display error message if any
    errorMessage?.let {
        Text(
            text = it,
            color = Color.Red,
            modifier = Modifier.padding(bottom = 15.dp)
        )
    }

    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        onClick = {
            viewModel.signup(email, password, confirmPassword)
        },
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(size = 4.dp),
        enabled = email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
    ) {
        Text(
            text = "Sign up",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}


@Composable
private fun SignUpBottomSection(onClick: () -> Unit) {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    color = Color(0xFF94A3B8),
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal
                )
            ) {
                append("Already have an account?  ")
            }

            withStyle(
                style = SpanStyle(
                    color = uiColor,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline
                )
            ) {
                append("Login Now")
            }

        }, modifier = Modifier.clickable { onClick() })
    }
}


@Composable
private fun SignUpTopSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Box(contentAlignment = Alignment.TopCenter) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.46f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier.padding(top = 80.dp), verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(42.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                tint = uiColor
            )

            Spacer(modifier = Modifier.width(15.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = uiColor
                )

                Text(
                    text = stringResource(id = R.string.lets_get_started),
                    style = MaterialTheme.typography.titleMedium,
                    color = uiColor
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(top = 20.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.signup),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )

    }
}