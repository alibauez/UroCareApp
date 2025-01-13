package com.example.urocareapp.chatbot

import android.content.Intent
import android.view.LayoutInflater
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.urocareapp.R
import com.example.urocareapp.chatbot.ui.theme.ColorModelMessage
import com.example.urocareapp.chatbot.ui.theme.ColorUserMessage
import com.example.urocareapp.chatbot.ui.theme.verde
import com.example.urocareapp.HomePaciente
import com.example.urocareapp.PerfilPaciente
import com.example.urocareapp.AuthActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@RequiresApi(35)
@Composable
fun ChatPage(modifier: Modifier = Modifier, viewModel: ChatViewModel) {
    Column(
        modifier = modifier
    ) {
        AppHeader() // Agrega  Toolbar
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList
        )
        MessageInput(
            onMessageSend = {
                viewModel.sendMessage(it)
            }
        )
    }
}

@Composable
fun MessageList(modifier: Modifier = Modifier, messageList: List<MessageModel>) {
    if (messageList.isEmpty()) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(60.dp),
                painter = painterResource(id = R.drawable.baseline_question_answer_24),
                contentDescription = "Icon",
                tint = verde,
            )
            Text(text = "Pregúntame algo", fontSize = 22.sp)
        }
    } else {
        LazyColumn(
            modifier = modifier,
            reverseLayout = true
        ) {
            items(messageList.reversed()) {
                MessageRow(messageModel = it)
            }
        }
    }
}

@Composable
fun MessageRow(messageModel: MessageModel) {
    val isModel = messageModel.role == "model"

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .align(if (isModel) Alignment.BottomStart else Alignment.BottomEnd)
                    .padding(
                        start = if (isModel) 8.dp else 70.dp,
                        end = if (isModel) 70.dp else 8.dp,
                        top = 8.dp,
                        bottom = 8.dp
                    )
                    .clip(RoundedCornerShape(48f))
                    .background(if (isModel) ColorModelMessage else ColorUserMessage)
                    .padding(16.dp)
            ) {
                SelectionContainer {
                    Text(
                        text = messageModel.message,
                        fontWeight = FontWeight.W500,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember {
        mutableStateOf("")
    }

    Row(
        modifier = Modifier.padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = message,
            onValueChange = {
                message = it
            }
        )
        IconButton(onClick = {
            if (message.isNotEmpty()) {
                onMessageSend(message)
                message = ""
            }
        }) {
            Icon(
                imageVector = Icons.Default.Send,
                contentDescription = "Enviar"
            )
        }
    }
}

@Composable
fun AppHeader() {
    val context = LocalContext.current

    // Usamos AndroidView para agregar el Toolbar desde XML
    AndroidView(
        factory = { ctx ->
            LayoutInflater.from(ctx).inflate(R.layout.toolbar_paciente, null) as androidx.appcompat.widget.Toolbar
        },
        modifier = Modifier.fillMaxWidth(),
        update = { toolbar ->
            // Configurar el título del toolbar
            toolbar.title = "UroCareApp"

            // Inflar el menú
            toolbar.inflateMenu(R.menu.menu_toolbar)

            // Configuramos el listener para las opciones del menú
            toolbar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.item_home -> {
                        // Acción para "Inicio"
                        context.startActivity(Intent(context, HomePaciente::class.java))
                        true
                    }
                    R.id.item_salir -> {
                        // Acción para "Cerrar sesión"
                        Firebase.auth.signOut()
                        context.startActivity(Intent(context, AuthActivity::class.java))
                        true
                    }
                    R.id.item_perfil -> {
                        // Acción para "Perfil"
                        context.startActivity(Intent(context, PerfilPaciente::class.java))
                        true
                    }
                    else -> false
                }
            }
        }
    )
}