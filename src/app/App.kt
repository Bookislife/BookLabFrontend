package app

import react.*
import react.dom.*
import org.w3c.dom.*
import kotlinext.js.*
import kotlinx.html.*
import kotlinx.html.js.*
import kotlin.js.*

// 映射JavaScript模块axios到Kotlin代码中
@JsModule("axios")
external fun <T> axios(config: AxiosConfigSettings): Promise<AxiosResponse<T>>

external interface AxiosConfigSettings {
    var url: String
    var method: String
    var baseUrl: String
    var timeout: Number
    var data: dynamic
    var transferRequest: dynamic
    var transferResponse: dynamic
    var headers: dynamic
    var params: dynamic
    var withCredentials: Boolean
    var adapter: dynamic
    var auth: dynamic
    var responseType: String
    var xsrfCookieName: String
    var xsrfHeaderName: String
    var onUploadProgress: dynamic
    var onDownloadProgress: dynamic
    var maxContentLength: Number
    var validateStatus: (Number) -> Boolean
    var maxRedirects: Number
    var httpAgent: dynamic
    var httpsAgent: dynamic
    var proxy: dynamic
    var cancelToken: dynamic
}

external interface AxiosResponse<T> {
    val data: T
    val status: Number
    val statusText: String
    val headers: dynamic
    val config: AxiosConfigSettings
}

external interface Book {
    val bookName: String
    val author: String
}

external interface ApiResponse {
 val code: Int 
 val data: Array<Book>
}

// 对应React Props
interface AppProps:RProps{
}

// 对应React State
interface AppState:RState {
    var items: Array<Book>
    var errorMessage:String
}

// 对应React Component
class App(props:AppProps) : RComponent<AppProps, AppState>(props) {
      override fun AppState.init(props: AppProps) {
        items = arrayOf<Book>()
        errorMessage = ""
    }

    // 绘制每行数据
    private fun RBuilder.renderRow(book:Book) {
        tr {
            // 书名
            td {
                + book.bookName
            }
            // 作者名
            td {
                + book.author
            }
        }
    }

    // 对应React Component的render()方法
    override fun RBuilder.render() {
        div {
            attrs.jsStyle = js {
                width = "100%"
                height = "100%"
            }
            // 错误信息显示框
            p {
                // CSS样式
                attrs.jsStyle = js {
                    color = "red"
                }
                
                if(state.errorMessage!=null){
                    +state.errorMessage
                }
            }
            // 书籍列表
            table {
                // CSS样式
                attrs.jsStyle = js {
                    valign = "middle"
                    margin = "0 auto"
                }
                thead { 
                    tr { 
                        th { 
                            +"书名" 
                        } 
                        th { 
                            +"作者" 
                        } 
                    } 
                    }
                tbody{
                    state.items.map {
                        renderRow(it)
                    }
                }
            }
        }
    }

    // React Component的生命周期函数，每次组件被挂载后执行
     override fun componentDidMount() {
        // 创建axios配置项，这里配置了请求url和超时时间
        val config: AxiosConfigSettings = jsObject {
            url = "http://localhost:8080/api/books"
            timeout = 3000
        }

        // 发出网络请求并处理返回结果
        axios<ApiResponse>(config).then { response ->
            // 成功返回数据
            val apiResult: ApiResponse = response.data
            // 更新state，清空错误信息
            setState {
                items = apiResult.data
                errorMessage = ""
            }
            console.log(apiResult)
        }.catch { error ->
            // 返回异常数据
            // 更新state，更新错误信息
            setState {
                items = arrayOf<Book>()
                errorMessage = error.message ?: ""
            }
            console.log(error)
        }
    }
}

fun RBuilder.app() = child(App::class) {}