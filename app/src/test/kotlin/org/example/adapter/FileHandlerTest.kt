package org.example.adapter

import io.mockk.every
import io.mockk.mockk
import org.example.adapter.FileHandler
import org.junit.jupiter.api.Test

class FileHandlerTest {

    @Test
    fun `should save the image successfully`() {
        val filehandlerMock = mockk<FileHandler> {
            every { write(any(), any()) } returns "path/to/saved/file.png"
        }
    }
}