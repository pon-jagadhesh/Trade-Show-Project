async function fetchAPIList() {
    const response = await fetch("api-list"); // Fetch from backend
    const data = await response.json();
    const table = document.getElementById("api-list");
    table.innerHTML = "";

    data.forEach(api => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${api.name}</td>
            <td><code>${api.url}</code></td>
            <td>${api.methods.join(", ")}</td>
            <td>
                <button class="test-btn" onclick="testAPI('${api.url}')">Test</button>
            </td>
        `;
        table.appendChild(row);
    });
}

function getSelectedMethod() {
    return document.getElementById("method-selector").value;
}

function getRequestHeaders() {
    const headerKey = document.getElementById("request-header-key").value;
    const headerValue = document.getElementById("request-header-value").value;
    const headers = {};
    if (headerKey && headerValue) {
        headers[headerKey] = headerValue;
    }
    return headers;
}

function getRequestBody(method) {
    if (["POST", "PUT", "PATCH", "DELETE"].includes(method)) {
        const requestBody = document.getElementById("request-body").value;
        try {
            return JSON.stringify(JSON.parse(requestBody));
        } catch (error) {
            throw new Error("Invalid JSON in request body");
        }
    }
    return null;
}

async function testAPI(url) {
    try {
        const method = document.getElementById("method-selector").value; // Get selected method
        const fullUrl = window.location.origin + "/SwaggerLite" + url;
        let options = { method: method, headers: {} };
        const requestParam= "?"+document.getElementById("request-param").value;

        // Display selected method before sending request
        document.getElementById("selected-method").innerText = `Calling ${method} on ${url}`;

        // Add header if provided
        const headerKey = document.getElementById("request-header-key").value;
        const headerValue = document.getElementById("request-header-value").value;
        if (headerKey && headerValue) {
            options.headers[headerKey] = headerValue;
        }

        // Add request body if method is POST
        if (method === "POST") {
            const requestBody = document.getElementById("request-body").value;
            options.headers["Content-Type"] = "application/json";
            options.body = requestBody;
        }

        // Add query parameters if provided
  
       
           let response = await fetch(fullUrl, options);
        const contentType = response.headers.get("content-type");

        let result;
        if (contentType && contentType.includes("application/json")) {
            result = await response.json();
            result = JSON.stringify(result, null, 2);
        } else {
            result = await response.text();
        }

        document.getElementById("response").innerText = result;
    } catch (error) {
        document.getElementById("response").innerText = "Request Failed: " + error;
    }
}

fetchAPIList();

