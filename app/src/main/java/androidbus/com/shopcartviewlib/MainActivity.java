package androidbus.com.shopcartviewlib;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import androidbus.com.shopcartlib.ShopCartView;

public class MainActivity extends AppCompatActivity implements ShopCartView.OnAddOrDelListner {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	Button endView = (Button) findViewById(R.id.endView);
	ShopCartView scv = (ShopCartView) findViewById(R.id.scv);
	scv.setCount(6);
	scv.setOnAddOrDelListner(this);
	scv.setCartAnim(true,this,scv,endView);
	scv.setIgnoreHintArea(false);
    }

    @Override
    public void onAddClick(int count) {

    }

    @Override
    public void onDelClick(int count) {

    }
}
