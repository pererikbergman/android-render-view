package se.uncle.renderview.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import se.uncle.renderview.RenderView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RenderView renderView = (RenderView)findViewById(R.id.render_view);
        renderView.setRenderEngine(new ExampleRender());
    }
}
