package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.HadithRepository
import com.example.model.Hadith
import com.example.ui.theme.*

@Composable
fun HadithCard(
  hadith: Hadith,
  onNextHadith: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current

  Card(
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    modifier = modifier
      .fillMaxWidth()
      .testTag("hadith_card")
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp)
    ) {
      // Top header with badge and Next button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(32.dp)
              .clip(CircleShape)
              .background(GeminiGradient),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.AutoAwesome,
              contentDescription = "Hadis Harian",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Hadis Harian Pilihan",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
          )
        }

        IconButton(
          onClick = onNextHadith,
          modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .testTag("next_hadith_button")
        ) {
          Icon(
            imageVector = Icons.Default.NavigateNext,
            contentDescription = "Hadis Seterusnya",
            tint = SleekPrimary,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Arabic text
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(
          text = hadith.arabicText,
          fontSize = 17.sp,
          fontWeight = FontWeight.SemiBold,
          lineHeight = 30.sp,
          color = MaterialTheme.colorScheme.onSurface,
          textAlign = TextAlign.Right,
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp)
        )
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Malay translation
      Text(
        text = "\"${hadith.translationMs}\"",
        fontSize = 14.sp,
        fontStyle = FontStyle.Italic,
        color = MaterialTheme.colorScheme.onSurface,
        lineHeight = 22.sp
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Narrator & Source reference
      Text(
        text = "— ${hadith.narrator} • ${hadith.sourceBook}",
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Action row: Share button & Copy button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedButton(
          onClick = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Hadis", HadithRepository.formatForSharing(hadith))
            clipboard.setPrimaryClip(clip)
            Toast.makeText(context, "Hadis disalin ke papan klip!", Toast.LENGTH_SHORT).show()
          },
          shape = RoundedCornerShape(14.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
          modifier = Modifier.height(38.dp).testTag("copy_hadith_button")
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Salin Teks",
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Salin", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        }

        Spacer(modifier = Modifier.width(10.dp))

        Button(
          onClick = {
            HadithRepository.shareHadith(context, hadith)
          },
          colors = ButtonDefaults.buttonColors(
            containerColor = SleekPrimary,
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(14.dp),
          contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
          modifier = Modifier.height(38.dp).testTag("share_hadith_button")
        ) {
          Icon(
            imageVector = Icons.Default.Share,
            contentDescription = "Kongsi ke Media Sosial",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "Kongsi Hadis", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

