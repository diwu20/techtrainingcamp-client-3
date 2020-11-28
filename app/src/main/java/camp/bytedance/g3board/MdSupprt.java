package camp.bytedance.g3board;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import io.noties.markwon.Markwon;
import io.noties.markwon.image.ImagesPlugin;

public class MdSupprt {

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

    public void showMdString(Context context, String mdtext, TextView textView) {
        final Markwon markwon = Markwon.builder(context)
                .usePlugin(ImagesPlugin.create())
                .build();
        markwon.setMarkdown(textView, replaceMark(mdtext, 0));
    }
}
