package camp.bytedance.g3board;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.ImagesPlugin;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 用于支持MarkDown文本的解析展示，
 * @method replaceMark 用于将Markdown文本字符串中的插图标替换为完整的URL
 * @method showMdString 调用markwon包中的相关方法，展示MarkDown图文混排结果
 *
 */
public class MdSupprt {

    /**替换图片关键字*/
    private String replaceMark(String mdtext, int start) {
        int indexStart = mdtext.indexOf("![", start);
        if (indexStart == -1) {
            Log.d("replaceMarkk_noreplace",mdtext);
            return mdtext;
        }
        int indexEnd = mdtext.indexOf(")", indexStart);
        String subText = mdtext.substring(indexStart, indexEnd + 1);
        Log.d("replaceMarkk_subText",subText);
        int fileNameStart = subText.indexOf("(");
        String fileName = subText.substring(fileNameStart + 1, subText.length() - 1);
        String imageMark = "  \n![](http://cdn.skyletter.cn/" + fileName +")  \n";
        Log.d("replaceMarkk_imageMark",imageMark);
        mdtext.replace(subText,imageMark);
        Log.d("replaceMarkk_replace",mdtext);
        return replaceMark(mdtext.replace(subText,imageMark), indexEnd);
    }

    /**调用Markwon的swtMarkdown方法显示富文本*/
    public void showMdString(Context context, String mdtext, TextView textView) {
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(ImagesPlugin.create())
                .build();
        markwon.setMarkdown(textView, replaceMark(mdtext, 0));
    }
}
