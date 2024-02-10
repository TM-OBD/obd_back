package com.isyb.obd.rest;

import com.isyb.obd.models.repos.EngineInfoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static com.isyb.obd.util.Sources.INFO_V1;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class EngineInfoControllerV1Test {

    @Mock
    private EngineInfoRepository engineInfoRepository;

    private MockMvc mockMvc;
    //    private ObjectMapper objectMapper;
    @InjectMocks
    private EngineInfoControllerV1 engineInfoControllerV1;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(engineInfoControllerV1).build();
//        this.objectMapper = new ObjectMapper();
    }

    @ParameterizedTest
    @MethodSource("com.isyb.obd.rest.EngineInfoControllerV1Test#argumentsStream")
    void handleInfoV1(String info, ResultMatcher expectedStatus) throws Exception {
        mockMvc.perform(
                post(INFO_V1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(info)
        ).andExpect(expectedStatus);
    }

    static Stream<Arguments> argumentsStream() {
        return Stream.of(
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,24:1200,82:30", status().isOk()),
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,20:1;1;1,25:1;1;1,24:1200,82:30,a:1;1;1", status().isBadRequest()),
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,a:12,24:1200,82:30", status().isBadRequest()),
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,24:1200", status().isBadRequest()),
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,24:1200,", status().isBadRequest()),
                Arguments.of("0:2041917982", status().isBadRequest()),
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,24:1200,82:30,c:unknown_value", status().isBadRequest()),
                Arguments.of("", status().isBadRequest()),
                Arguments.of("0:,:45.3535032,b:,24:1200", status().isBadRequest()),
                Arguments.of("0:2041917982;a:45.3535032;b:28.6902346;24:1200;82:30", status().isBadRequest()),
                Arguments.of("0:2041917982;a:45.3535032;b:28.6902346;24:1200;82:30", status().isBadRequest()),
                Arguments.of("0:2041917982,a:'45.3535032',b:\"28.6902346\",24:1200,82:30", status().isBadRequest()),
                Arguments.of("0:2041917982,a: 45.3535032 ,b:28.6902346,24:1200,82:30", status().isBadRequest()),
//                TODO: ложноположительный результат
                Arguments.of("0:2041917982,a:45.3535032,b:28.6902346,24:1200,82:30".repeat(100), status().isBadRequest())
        );
    }
}