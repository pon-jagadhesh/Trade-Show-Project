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
        const method = getSelectedMethod();
        let fullUrl = window.location.origin + "/SwaggerLite" + url;
        let options = { method: method, headers: getRequestHeaders() };

        // Display selected method before sending request
        document.getElementById("selected-method").innerText = `Calling ${method} on ${url}`;

        // Add request body if method is POST, PUT, PATCH, or DELETE
        if (["POST", "PUT", "PATCH", "DELETE"].includes(method)) {
            const requestBody = getRequestBody(method);
            if (requestBody) {
                options.headers["Content-Type"] = "application/json";
                options.body = requestBody;
            }
        }

        // Add request parameters if provided
        const requestParams = getRequestParams();
        if (requestParams) {
            const queryString = new URLSearchParams(requestParams).toString();
            fullUrl += fullUrl.includes("?") ? "&" + queryString : "?" + queryString;
        }

        console.log("Full URL:", fullUrl); // Debug log

        let response = await fetch(fullUrl, options);
        const contentType = response.headers.get("content-type");

        let result = `Status: ${response.status} ${response.statusText}\n\n`;

        if (contentType && contentType.includes("application/json")) {
            const jsonResult = await response.json();
            result += JSON.stringify(jsonResult, null, 2);
        } else {
            result += await response.text();
        }

        document.getElementById("response").innerText = result;
    } catch (error) {
        document.getElementById("response").innerText = `Request Failed: ${error.message}\n\nStack Trace:\n${error.stack}`;
    }
}

function getRequestParams() {
    const paramInput = document.getElementById("request-param").value.trim();
    if (!paramInput) return null;

    const params = {};
    paramInput.split('&').forEach(param => {
        const [key, value] = param.split('=');
        if (key && value) {
            params[decodeURIComponent(key)] = decodeURIComponent(value);
        }
    });

    return Object.keys(params).length > 0 ? params : null;
}

fetchAPIList();
console.log("hello");

