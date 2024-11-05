package com.example.chatbotappv2.fake

import com.example.chatbotappv2.ui.chat.Message
import com.example.chatbotappv2.ui.chat.Role

class FakeDataDatasource {
    companion object {
        fun loadMessageList(): List<Message> {
            return listOf(
                Message("Hello there", Role.user),
                Message("What can i help you", Role.model),
                Message("What is the weather today ?", Role.user),
                Message("Today is sunny", Role.model),
                Message("Tell me a joke", Role.user),
                Message("Sure here is a joke...", Role.model),
                Message(
                    """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. 
                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
                when an unknown printer took a galley of type and scrambled it to make a type 
                specimen book. It has survived not only five centuries, but also the leap into 
                electronic typesetting, remaining essentially unchanged. It was popularised in the 
                1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more
                 recently with desktop publishing software like Aldus PageMaker including versions 
                 of Lorem Ipsum.
            """.trimIndent(), Role.user
                ),
                Message(
                    """
                Lorem Ipsum is simply dummy text of the printing and typesetting industry. 
                Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, 
                when an unknown printer took a galley of type and scrambled it to make a type 
                specimen book. It has survived not only five centuries, but also the leap into 
                electronic typesetting, remaining essentially unchanged. It was popularised in the 
                1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more
                 recently with desktop publishing software like Aldus PageMaker including versions 
                 of Lorem Ipsum.
            """.trimIndent(), Role.model
                )
            )
        }
    }
}