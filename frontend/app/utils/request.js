/**
 * Parses the JSON returned by a network request
 *
 * @param  {object} response A response from a network request
 *
 * @return {object}          The parsed JSON from the request
 */
function parseJSON(response) {
  if (response.status === 204 || response.status === 205) {
    return null;
  }
  return response.json();
}

/**
 * Checks if a network request came back fine, and throws an error if not
 *
 * @param  {object} response   A response from a network request
 *
 * @return {Promise} Returns a promise that resolves to response or rejects with error
 */
async function checkStatus(response) {
  if (response.status >= 200 && response.status < 300) {
    return response;
  }

  // Hata durumunda response body'sini parse et
  let errorMessage = response.statusText;
  let errorData = null;
  
  try {
    const contentType = response.headers.get('content-type');
    if (contentType && contentType.includes('application/json')) {
      // Response body'yi clone edip parse etmeliyiz çünkü bir kez okunabilir
      errorData = await response.clone().json();
      if (errorData) {
        // Backend'den gelen hata mesajı "hata" veya "message" alanında olabilir
        errorMessage = errorData.hata || errorData.message || errorData.error || response.statusText;
      }
    }
  } catch (e) {
    // JSON parse hatası, statusText kullanılacak
    console.error('Error parsing error response:', e);
  }

  const error = new Error(errorMessage);
  error.response = response;
  error.data = errorData;
  throw error;
}

/**
 * Gets auth token from localStorage
 */
function getAuthToken() {
  return localStorage.getItem('authToken');
}

/**
 * Requests a URL, returning a promise
 *
 * @param  {string} url       The URL we want to request
 * @param  {object} [options] The options we want to pass to "fetch"
 *
 * @return {object}           The response data
 */
export default async function request(url, options = {}) {
  const token = getAuthToken();
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  const response = await fetch(url, {
    ...options,
    headers,
  });
  
  const checkedResponse = await checkStatus(response);
  return parseJSON(checkedResponse);
}
