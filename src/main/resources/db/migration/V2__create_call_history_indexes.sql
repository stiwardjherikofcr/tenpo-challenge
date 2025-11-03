CREATE INDEX idx_timestamp ON call_history (timestamp DESC);
CREATE INDEX idx_endpoint ON call_history (endpoint);
CREATE INDEX idx_success ON call_history (success);

CREATE INDEX idx_request_params_gin ON call_history USING GIN (request_params);
CREATE INDEX idx_response_gin ON call_history USING GIN (response);
