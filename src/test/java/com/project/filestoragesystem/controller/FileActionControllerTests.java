package com.project.filestoragesystem.controller;

import com.project.filestoragesystem.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileActionController.class)
public class FileActionControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileRepository fileRepository;

    @Test
    public void whenInvalidContentType_thenFailsToCreateFile() throws Exception {
        byte[] testData = "TestData".getBytes();
        mockMvc.perform(post("/api/file/create")
                .content(testData))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void whenInvalidContentType_thenFailsToUpdateFile() throws Exception {
        byte[] testData = "TestData".getBytes();
        mockMvc.perform(post("/api/file/update")
                .content(testData))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }

    @Test
    public void whenFileNameIsProvided_thenDeletesFile() throws Exception {
        mockMvc.perform(delete("/api/file/delete?name=file.dat"))
                .andExpect(status().isOk());

        verify(fileRepository, times(1)).delete("file.dat");
    }

    @Test
    public void whenFileNameIsMissing_thenFailsToDeleteFile() throws Exception {
        mockMvc.perform(delete("/api/file/delete"))
                .andExpect(status().isBadRequest());
    }
}
