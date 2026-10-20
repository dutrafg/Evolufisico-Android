package br.com.evolufisico.android

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.os.Build
import android.os.CancellationSignal
import android.provider.Settings
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Bloqueio de privacidade. Autenticação é efetuada exclusivamente pelo sistema
 * (PIN/padrão/senha; biometria onde disponível); não captura credenciais.
 * Não substitui autorização criptográfica por operação nem segurança do SO.
 */
class DeviceSessionGate(private val activity: ComponentActivity) {
    var unlocked by androidx.compose.runtime.mutableStateOf(false)
        private set
    var message by androidx.compose.runtime.mutableStateOf<String?>(null)
        private set
    private var checking = false
    private var externalDocumentFlow = false

    private val credentialResult = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        checking = false
        if (result.resultCode == Activity.RESULT_OK) {
            unlocked = true
            message = null
        } else {
            unlocked = false
            message = "A autenticação foi cancelada."
        }
    }

    fun isExternalDocumentFlow(): Boolean = externalDocumentFlow
    fun externalDocumentFlow(active: Boolean) { externalDocumentFlow = active }

    fun lock() {
        unlocked = false
        message = null
    }

    fun backgrounded() {
        if (!externalDocumentFlow) lock()
    }

    fun openSecuritySettings() {
        activity.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    }

    fun requestUnlock() {
        if (checking) return
        val manager = activity.getSystemService(KeyguardManager::class.java)
        if (manager == null || !manager.isDeviceSecure) {
            message = "Defina PIN, senha ou padrão de bloqueio nas configurações do Android."
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            checking = true
            try {
                val prompt = BiometricPrompt.Builder(activity)
                    .setTitle("Desbloquear EvoluFísico")
                    .setSubtitle("Confirme sua identidade no sistema Android")
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                    .build()
                prompt.authenticate(CancellationSignal(), activity.mainExecutor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            checking = false
                            unlocked = true
                            message = null
                        }
                        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                            checking = false
                            unlocked = false
                            message = "Não foi possível autenticar. Tente novamente."
                        }
                    })
            } catch (_: Exception) {
                checking = false
                credentialOnly(manager)
            }
        } else {
            // Android 10: confirmação padrão do sistema para manter compatibilidade.
            credentialOnly(manager)
        }
    }

    @Suppress("DEPRECATION")
    private fun credentialOnly(manager: KeyguardManager) {
        val intent = manager.createConfirmDeviceCredentialIntent(
            "Desbloquear EvoluFísico", "Confirme o bloqueio de tela do dispositivo."
        )
        if (intent == null) {
            message = "Bloqueio do dispositivo indisponível."
            return
        }
        checking = true
        credentialResult.launch(intent)
    }
}

@Composable
fun DeviceGateScreen(message: String?, onUnlock: () -> Unit, onSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("EvoluFísico", style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Seus dados estão protegidos. Confirme sua identidade para acessar o aplicativo.")
        Spacer(Modifier.height(20.dp))
        Button(onClick = onUnlock, modifier = Modifier.fillMaxWidth()) {
            Text("Desbloquear")
        }
        OutlinedButton(onClick = onSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Configurar bloqueio de tela")
        }
        message?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))
        Text("É necessário configurar a segurança de bloqueio do Android. " +
            "Os registros permanecem locais e criptografados.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall)
    }
}
