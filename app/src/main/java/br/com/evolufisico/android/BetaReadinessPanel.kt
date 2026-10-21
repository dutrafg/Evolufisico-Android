package br.com.evolufisico.android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Indicadores de liberação: não marcar testes como aprovados sem evidência. */
@Composable
fun BetaReadinessPanel(evidence:BetaEvidence=BetaEvidence()) {
    val missing=BetaReadiness.pending(evidence)
    Card(modifier=Modifier.fillMaxWidth(),shape=RoundedCornerShape(12.dp),
        colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(14.dp),verticalArrangement=Arrangement.spacedBy(7.dp)) {
            Text("Prontidão para a Beta",style=MaterialTheme.typography.titleMedium)
            Text(if(missing.isEmpty()) "Critérios registrados como atendidos. Validar evidências."
                else "Ainda não liberável: "+missing.size+" critérios sem comprovação.",
                style=MaterialTheme.typography.bodyMedium,
                color=if(missing.isEmpty()) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error)
            missing.forEach { check ->
                Text("• "+check.title,style=MaterialTheme.typography.bodySmall)
            }
            Text("A verificação do GitHub e a homologação em aparelho físico devem ser " +
                 "confirmadas antes de distribuir dados reais.",
                 style=MaterialTheme.typography.bodySmall,
                 color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
