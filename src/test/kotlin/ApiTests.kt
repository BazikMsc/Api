import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import junit.framework.TestCase.*
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.example.PersonInfo
import org.junit.Test

class ApiTests {

       private val client = HttpClient(CIO) {
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.BODY
            }
            install(ContentNegotiation) {
                json()
            }
        }

    @Test
    fun getAgeByNameWithValidName() {
        runTest {
            val getRequest = client.get("https://api.agify.io?name=Michael").bodyAsText()
            val response: PersonInfo = Json.decodeFromString(getRequest)
            assertNotNull(response)
            assertEquals("Michael", response.name)
            assertTrue((response.age ?: 0) > 0)
        }
    }

    @Test
    fun getAgeByNameWithInvalidName() {
        runTest {
            val getRequest = client.get("https://api.agify.io?name=12345").bodyAsText()
            val response: PersonInfo = Json.decodeFromString(getRequest)
            assertNotNull(response)
            assertEquals("12345", response.name)
            assertNull(response.age)
        }
    }

    @Test
    fun getStatisticsByNameWithValidName() {
        runTest {
            val getRequest = client.get("https://api.agify.io?name=John").bodyAsText()
            val response: PersonInfo = Json.decodeFromString(getRequest)
            assertNotNull(response)
            assertEquals("John", response.name)
            assertTrue((response.count ?: 0) > 0)
        }
    }

    @Test
    fun getStatisticsByNameWithInvalidName() {
        runTest {
            val getRequest = client.get("https://api.agify.io?name=invalid_name_123").bodyAsText()
            val response: PersonInfo = Json.decodeFromString(getRequest)
            assertNotNull(response)
            assertEquals("invalid_name_123", response.name)
            assertNull(response.age)
        }
    }

    @Test
    fun postUrlError() {
        runTest {
            val response = client.post("https://api.agify.io?name=John")
            assertEquals(404, response.status.value)
        }
    }

    @Test
    fun putUrlError() {
        runTest {
            val response = client.post("https://api.agify.io?name=John")
            assertEquals(404, response.status.value)
        }
    }

    @Test
    fun unprocessableContent() {
        runTest {
            val response = client.get("https://api.agify.io?family=John")
            assertEquals(422, response.status.value)
        }
    }

    @Test
    fun multipleNames() {
        runTest {
            val getRequest = client.get("https://api.agify.io?name[]=anna&name[]=petr&name[]=ivan").bodyAsText()
            val response: List<PersonInfo> = Json.decodeFromString(getRequest)
            assertNotNull(response)
            assertEquals("anna", response[0].name)
            assertNotNull(response[0].age)
            assertNotNull(response[0].count)
            assertEquals("petr", response[1].name)
            assertNotNull(response[1].age)
            assertNotNull(response[1].count)
            assertEquals("petr", response[2].name)
            assertNotNull(response[2].age)
            assertNotNull(response[2].count)
        }
    }
}
