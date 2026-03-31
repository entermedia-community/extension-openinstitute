# Automation Agent System Architecture

## Overview

The automation agent system provides a framework for executing AI-powered automation scenarios through a combination of XML configuration files and Java implementations.

## Database Relationships

### 1. Agent Definitions (`automationagent`)

**Location:** `/extension-openinstitute/serverside/webapp/WEB-INF/base/finder/catalog/data/fields/automationagent/*.xml`

These XML files define the metadata and configuration for each automation agent type:

- **Purpose:** Define agent properties, capabilities, and UI configuration
- **Structure:** Field definitions for agent configuration forms
- **Relationship:** Maps to Java classes that extend `BaseAgent.java`

### 2. Enabled Agents List (`automationagentenabled`)

**Location:** `/entermedia-server/webapp/WEB-INF/base/finder/catalog/data/lists/automationagentenabled/*.xml`

These XML files control which agents are available and enabled:

- **Purpose:** Registry of active automation agents
- **Structure:** List of enabled agent identifiers
- **Relationship:** References agent definitions and their Java implementations

## Component Architecture

### Core Components

#### 1. BaseAgent.java
**Location:** `/entermedia-server/src/org/entermediadb/ai/BaseAgent.java`

- **Role:** Abstract base class for all automation agents
- **Responsibility:** Provides common agent functionality and lifecycle methods
- **Extension Pattern:** All concrete agent classes must extend this base

#### 2. AutomationManager.java
**Location:** `/entermedia-server/src/org/entermediadb/ai/automation/AutomationManager.java`

- **Role:** Orchestrates automation scenario execution
- **Key Method:** `runScenario()`
  - Loads scenario details from XML configuration
  - Instantiates appropriate agent classes
  - Executes agent workflow
  - Manages agent lifecycle and state

#### 3. RunModuleAgent.java
**Location:** `/entermedia-server/src/org/entermediadb/ai/automation/RunModuleAgent.java`

- **Role:** Generic agent for executing arbitrary Java code within automation scenarios
- **Use Case:** When a scenario step requires custom Java logic without creating a dedicated agent class
- **Functionality:**
  - Executes specified module/script code
  - Accesses full AgentContext and system resources
  - Returns results back to the scenario workflow
  - Provides flexibility for one-off or prototype automation steps

**When to Use RunModuleAgent:**
- Quick prototyping of automation logic
- Custom data processing that doesn't warrant a full agent implementation
- Calling existing Java modules/services from automation scenarios
- Bridging legacy code with the automation framework
- Testing and debugging automation workflows

**Example Scenario Configuration:**
```xml
<step>
  <agent>RunModuleAgent</agent>
  <module>/path/to/custom/Module.groovy</module>
  <parameters>
    <param1>value1</param1>
    <param2>${context.value}</param2>
  </parameters>
</step>
```

**Integration Pattern:**
````
<userPrompt>
Provide the fully rewritten file, incorporating the suggested code change. You must produce the complete file.
</userPrompt>

