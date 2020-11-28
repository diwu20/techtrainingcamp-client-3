package camp.bytedance.g3board;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 *
 * @author Bytedance Technical Camp, Client Group 3, 吴迪 & 王龙逊
 * @date 2020/11/29
 * @descripation 公告类，包含了公告的多个属性，和json文件中的定义相对应
 *
 */

public class Bulletin implements Parcelable {

    private int type;
    private String id;
    private String title;
    private String cover;
    private List<String> covers;
    private String author;
    private String publishTime;
    private String data;
    private Bitmap[] bitmap;

    public Bulletin(Bulletin bulletinPeice) {
         type = bulletinPeice.type;
         id = bulletinPeice.id;
         title = bulletinPeice.title;
         cover = bulletinPeice.cover;
         covers = bulletinPeice.covers;
         author = bulletinPeice.author;
         publishTime = bulletinPeice.publishTime;
         data = bulletinPeice.data;
         bitmap = bulletinPeice.bitmap;
    }
    public String getTitle() {
        return title;
    }
    public String getId() {
        return id;
    }
    public String getAuthor() {
        return author;
    }
    public String getTime() {
        return publishTime;
    }
    public String getCover() {
        return cover;
    }
    public List<String> getCovers() {
        return covers;
    }
    public int getType() {
        return type;
    }
    public String getContent() {
        return data;
    }
    public void setContent(String content) {
        data = content;
    }
    public void setBitmap(Bitmap[] bm) {
        bitmap = bm;
    }
    public Bitmap[] getBitmap() {
        return bitmap;
    }

    protected Bulletin(Parcel in) {
        type = in.readInt();
        id = in.readString();
        title = in.readString();
        cover = in.readString();
        covers = in.createStringArrayList();
        author = in.readString();
        publishTime = in.readString();
        data = in.readString();
        bitmap = in.createTypedArray(Bitmap.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(cover);
        dest.writeStringList(covers);
        dest.writeString(author);
        dest.writeString(publishTime);
        dest.writeString(data);
        dest.writeTypedArray(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Bulletin> CREATOR = new Creator<Bulletin>() {
        @Override
        public Bulletin createFromParcel(Parcel in) {
            return new Bulletin(in);
        }

        @Override
        public Bulletin[] newArray(int size) {
            return new Bulletin[size];
        }
    };


}
