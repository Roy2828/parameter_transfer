# parameter_transfer
fragment activity携带参数注解解析库

使用 添加依赖库

```
 repositories { 
        maven { url "https://jitpack.io" }
    }
    
 implementation 'com.github.Roy2828:parameter_transfer:0.0.8'

 kapt 'com.github.Roy2828.parameter_transfer:compiler:0.0.8'
```


 例子：
 public class MainActivity2 extends AppCompatActivity {

    @ArgumentsField(value = "bb")
    public String name;
    @ArgumentsField(value = "ss")
    public String names;
    @ArgumentsField
    public int age;
    
    @ArgumentsField(isSerialize = SerializeMode.Serializable)
    public Bean bean;
    @ArgumentsField(value = "parcelable",isSerialize =  SerializeMode.Parcelable)
    public ParcelableBean parcelableBean;
    
    public static void launch(Context context,String bb,String ss,int age,Bean bean,ParcelableBean parcelable){
        Intent intent = new Intent(context,MainActivity2.class);
        intent.putExtra("bb",bb);
        intent.putExtra("ss",ss);
        intent.putExtra("age",age);
        intent.putExtra("bean",bean);
        intent.putExtra("parcelable",parcelable);
        context.startActivity(intent);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ParameterManager.getParameterManager().inject(this); 
    }
}



```
代码生成：
 public class MainActivity2$$$$$$Arguments implements ArgumentsApi {
  @Override
  public void inject(Object object) {
    if(object==null) {
      return;
    }
    MainActivity2 activity = (MainActivity2)object;
    assignment(activity,BundleUtils$$$$$$.getBundle(object));
  }

  

  public void assignment(MainActivity2 activity, Bundle bundle) {
    if(bundle !=null) {
      activity.name = bundle.getString("bb");
      activity.names = bundle.getString("ss");
      activity.age = bundle.getInt("age");
      activity.bean = (com.roy.parameter_transfer.Bean)bundle.getSerializable("bean");
      activity.parcelableBean = (com.roy.parameter_transfer.ParcelableBean)bundle.getParcelable("parcelable");
    }
  }
}


public class BundleUtils$$$$$$ {
  public static Bundle getBundle(Object object) {
    Bundle bundle = null;
    if(object instanceof FragmentActivity) {
      Intent intent = ((FragmentActivity)object).getIntent();
      if(intent !=null) {
         bundle = intent.getExtras();
      }
    }
    else if(object instanceof Fragment) {
       bundle = ((Fragment)object).getArguments();
    }
    return bundle;
  }
}
```



