package com.example.groot.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groot.R
import com.example.groot.ui.theme.GROOTTheme

@Composable
fun RegistrationScreen(
    navigateToHome: () -> Unit,
    viewModel: RegistrationViewModel = viewModel()
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        RegistrationContent(
            uiState = uiState,
            onUsernameChange = viewModel::onUsernameChange,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
            onPasswordVisibilityChange = viewModel::onPasswordVisibilityChange,
            onRegisterClick = { viewModel.signup(navigateToHome) },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        )
    }
}

@Composable
fun RegistrationContent(
    uiState: RegistrationUiState,
    onUsernameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        val enabled = !uiState.inProcess
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(modifier = Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.logo_nm),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.login_description),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Left,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.note),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(20.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(id = R.string.registration),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.username,
                        onValueChange = { onUsernameChange(it) },
                        singleLine = true,
                        enabled = enabled,
                        label = { Text(stringResource(id = R.string.user_name)) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = "Username Icon")
                        },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.email,
                        onValueChange = { onEmailChange(it) },
                        singleLine = true,
                        enabled = enabled,
                        label = { Text(stringResource(id = R.string.email)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email Icon")
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = uiState.password,
                        singleLine = true,
                        enabled = enabled,
                        onValueChange = { onPasswordChange(it) },
                        label = { Text(text = stringResource(id = R.string.password)) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Lock Icon")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = uiState.confirmPassword,
                        singleLine = true,
                        enabled = enabled,
                        onValueChange = { onConfirmPasswordChange(it) },
                        label = { Text(text = stringResource(id = R.string.conform_password)) },
                        visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { onPasswordVisibilityChange() }) {
                                Icon(
                                    imageVector = if (uiState.isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (uiState.isPasswordVisible) "Hide password " else "Show password"
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Password, contentDescription = "Lock Icon")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onRegisterClick,
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = enabled,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = stringResource(id = R.string.register), fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
            Spacer(Modifier.height(20.dp))
        }
        if (uiState.inProcess) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    GROOTTheme {
        Surface {
            RegistrationContent(
                uiState = RegistrationUiState(),
                onUsernameChange = {},
                onEmailChange = {},
                onPasswordChange = {},
                onConfirmPasswordChange = {},
                onPasswordVisibilityChange = {},
                onRegisterClick = {},
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}