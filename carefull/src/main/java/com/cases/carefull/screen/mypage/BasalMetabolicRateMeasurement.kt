package com.openstudy.carefull.screen.mypage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openstudy.carefull.R
import com.openstudy.carefull.common.BottomNavigationBar
import com.openstudy.carefull.common.MenuButton
import com.openstudy.carefull.common.RowLine
import com.openstudy.carefull.ui.theme.CarefullTheme

@Composable
fun BasalMetabolicRateMeasurement() {
    var selectedGender by remember { mutableStateOf(Gender.MALE) }
    var basalMetabolicRateKcal = "1500" //더미
    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentRoute = R.string.mypage)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = stringResource(id = R.string.basal_metabolic_rate_measurement),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(70.dp))
            Row(
                modifier = Modifier.clickable { selectedGender = Gender.MALE },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.gender),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.fillMaxWidth(0.38f))
                RadioButton(
                    selected = (selectedGender == Gender.MALE),
                    onClick = { selectedGender = Gender.MALE }
                )
                Text(
                    text = "남성",
                    style = MaterialTheme.typography.bodyLarge
                )

                RadioButton(
                    selected = (selectedGender == Gender.FEMALE),
                    onClick = { selectedGender = Gender.FEMALE }
                )
                Text(
                    text = "여성",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            MenuButton(text = stringResource(R.string.height), onClick = { })
            MenuButton(text = stringResource(R.string.weight), onClick = { })
            MenuButton(text = stringResource(R.string.age), onClick = { })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(0.6f),
                    text = stringResource(R.string.basal_metabolic_rate),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )

                Text(
                    modifier = Modifier.weight(0.2f),
                    text = basalMetabolicRateKcal,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )

                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.kcal),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )
            }
            RowLine()
            MenuButton(text = stringResource(R.string.activity_setting), onClick = { })
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(0.6f),
                    text = stringResource(R.string.activity_setting),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )

                Text(
                    modifier = Modifier.weight(0.2f),
                    text = basalMetabolicRateKcal,
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )

                Text(
                    modifier = Modifier,
                    text = stringResource(R.string.kcal),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.End
                )
            }
            RowLine()
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = {}, modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White
                )
            )
            {
                Text(
                    text = stringResource(R.string.save),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

enum class Gender {
    MALE, FEMALE
}

@Preview(showBackground = true)
@Composable
fun BasalMetabolicRateMeasurementPreview() {
    CarefullTheme {
        BasalMetabolicRateMeasurement()
    }
}