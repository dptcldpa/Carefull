package com.openstudy.carefull.screen.auth


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.openstudy.carefull.R
import com.openstudy.carefull.common.CustomTextField
import com.openstudy.carefull.ui.theme.CarefullTheme


@Composable
fun Signup() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordRepeat by remember { mutableStateOf("") }
    var nickName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            modifier = Modifier
                .padding(top = 150.dp),
            text = stringResource(R.string.signup),
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(60.dp))

        CustomTextField(
            value = email,
            onValueChange = { email = it },
            labelResId = R.string.email,
            placeholderResId = R.string.email_input,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomTextField(
            value = password,
            onValueChange = { password = it },
            labelResId = R.string.password,
            placeholderResId = R.string.password_input,
            keyboardType = KeyboardType.Password,
            isPasswordTextField = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomTextField(
            value = passwordRepeat,
            onValueChange = { passwordRepeat = it },
            labelResId = R.string.password_repeat,
            placeholderResId = R.string.password_repeat_input,
            keyboardType = KeyboardType.Password,
            isPasswordTextField = true
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomTextField(
            value = nickName,
            onValueChange = { nickName = it },
            labelResId = R.string.nickname,
            placeholderResId = R.string.nickname_input,
            keyboardType = KeyboardType.Text
        )

        Spacer(modifier = Modifier.height(60.dp))

        Button(
            onClick = {

            }, modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White
            )
        )
        {
            Text(text = stringResource(R.string.signup))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SignupPreview() {
    CarefullTheme {
        Signup()
    }
}