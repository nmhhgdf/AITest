import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.Test;

public class TestChat {

    @Test
    public void test01() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();

        String answer = model.chat("你好");
        System.out.println(answer);
    }

    @Test
    public void test02() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .baseUrl("https://api.deepseek.com")
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .modelName("deepseek-chat")
//                .modelName("deepseek-reasoner")
                .build();

        String answer = model.chat("你好");

        System.out.println(answer);
    }

    @Test
    public void test03() {
        ChatLanguageModel model = QwenChatModel.builder()
                .apiKey(System.getenv("DASHSCOPE_API_KEY"))
                .modelName("qwen-max")
                .build();

        String answer = model.chat("你好");

        System.out.println(answer);
    }

}
