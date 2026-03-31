# Automation Agent System Architecture

## Notes

- Agent implementations are loosely coupled through XML configuration
- System supports dynamic agent discovery and loading
- Configuration changes don't require code recompilation
- Agent lifecycle managed centrally by AutomationManager

## Example Agent Implementations

### 1. AutomationEventHandler.java

**Location:** `/entermedia-server/src/org/entermediadb/ai/automation/AutomationEventHandler.java`

**Purpose:** Event-driven agent that responds to system events and user actions

**Trigger Mechanisms:**
- **Link-based triggers:** URL parameters that invoke automation
- **Event Manager integration:** System events (file upload, status change, etc.)
- **Scheduled triggers:** Timer-based automation execution

**Key Responsibilities:**

1. **Event Detection**
   - Listens for configured event types
   - Filters events based on criteria
   - Routes events to appropriate scenarios

2. **Parameter Extraction**
   - Extracts data from event context
   - Parses URL parameters from links
   - Retrieves user/session information

3. **AgentContext Population**
   - Copies relevant parameters to AgentContext
   - Sets execution scope and permissions
   - Provides data access to agents

**Parameter Mapping Flow:**