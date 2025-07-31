window.onload = function() {
  // Detecta o contexto dinamicamente e monta a URL para o OpenAPI JSON
  var path = window.location.pathname;
  var context = path.split('/').filter(Boolean)[0] || '';
  var apiUrl = context ? ('/' + context + '/api/openapi.json') : '/api/openapi.json';
  window.ui = SwaggerUIBundle({
    url: apiUrl,
    dom_id: '#swagger-ui',
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    layout: "StandaloneLayout"
  });
};
