package com.github.asharma1999.ohio_air_analysis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.document.Document;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;
import software.amazon.awssdk.services.bedrockruntime.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AviationAgentService {

    private final BedrockRuntimeClient bedrockClient;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Main orchestrator method handling the request lifecycle.
     */
    public String processUserPrompt(String userPrompt) {
        try {
            String jsonSchema = loadFlightSchema();
            ToolUseBlock toolUseBlock = invokeBedrockForToolUse(userPrompt, jsonSchema);

            if (toolUseBlock == null) {
                return "I couldn't understand which route or time period you were looking for. Please try asking: 'Tell me Q1 2025 data for Pittsburgh to London'.";
            }

            Map<String, Object> extractedParams = extractParameters(toolUseBlock);
            return executeQueryAndFormatResponse(extractedParams);

        } catch (Exception e) {
            log.error("Failed to process user prompt", e);
            return "Failed to process your request due to an internal error: " + e.getMessage();
        }
    }

    /**
     * Step 1: Loads the JSON schema configuration file from resources.
     */
    private String loadFlightSchema() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:schemas/flight-schema.json");
        return resource.getContentAsString(StandardCharsets.UTF_8);
    }

    /**
     * Step 2: Sends the prompt and tool configuration to Amazon Bedrock.
     */
    private ToolUseBlock invokeBedrockForToolUse(String userPrompt, String jsonSchemaString) {
        try {
            // 1. Parse JSON schema string into a Map using Jackson
            Map<String, Object> schemaMap = objectMapper.readValue(jsonSchemaString, Map.class);

            // 2. Build Tool Specification using Document.fromString
            ToolSpecification toolSpec = ToolSpecification.builder()
                    .name("query_international_flights")
                    .description("Extracts search parameters to query international flight statistics and load factors.")
                    .inputSchema(ToolInputSchema.builder()
                            .json(Document.fromString(jsonSchemaString))
                            .build())
                    .build();

            Tool tool = Tool.builder().toolSpec(toolSpec).build();
            ToolConfiguration toolConfig = ToolConfiguration.builder().tools(tool).build();

            ConverseRequest converseRequest = ConverseRequest.builder()
                    .modelId("anthropic.claude-3-5-sonnet-20241022-v2:0")
                    .system(SystemContentBlock.fromText("You are an expert aviation data assistant. Whenever a user asks about flight data or load factors, you MUST invoke the query_international_flights tool."))
                    .messages(Message.builder()
                            .role(ConversationRole.USER)
                            .content(ContentBlock.fromText(userPrompt))
                            .build())
                    .toolConfig(toolConfig)
                    .inferenceConfig(InferenceConfiguration.builder()
                            .temperature(0.0f)
                            .maxTokens(300)
                            .build())
                    .build();

            ConverseResponse response = bedrockClient.converse(converseRequest);
            ContentBlock outputBlock = response.output().message().content().get(0);

            // 3. Safely check for tool use using null-check
            if (outputBlock.toolUse() != null) {
                return outputBlock.toolUse();
            }

            return null;

        } catch (Exception e) {
            throw new RuntimeException("Failed to invoke Bedrock with tool configuration", e);
        }
    }

    /**
     * Step 3: Extracts the structured parameters map from Bedrock's tool response.
     */
    private Map<String, Object> extractParameters(ToolUseBlock toolUse) {
        return (Map<String, Object>) toolUse.input().unwrap();
    }

    /**
     * Step 4 & 5: Queries the database repository and formats the final response string.
     */
    private String executeQueryAndFormatResponse(Map<String, Object> params) {
        /*String origin = (String) params.get("origin");
        String destination = (String) params.get("destination");
        LocalDate startDate = LocalDate.parse((String) params.get("startDate"));
        LocalDate endDate = LocalDate.parse((String) params.get("endDate"));

        var results = flightRepository.getLoadFactor(origin, destination, startDate, endDate);

        if (results.isEmpty() || results.get(0)[2] == null) {
            return String.format("No flight records found for %s to %s between %s and %s.",
                    origin, destination, startDate, endDate);
        }

        Object[] row = results.get(0);
        Double loadFactor = (Double) row[2];

        return String.format("The load factor for flights from %s to %s for the requested period is %.2f%%.",
                origin, destination, loadFactor * 100);*/
        return "Placeholder for repository response formatting.";
    }
}