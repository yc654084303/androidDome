package view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import dome.android.yc.androiddome.R;


public class SlidingButtons extends View{
	private Bitmap gray_bg;//��ɫ�ı�������
	private Bitmap green_bg;//��ɫ�ı�������
	private Bitmap btn;//���
	private Bitmap num_price;//��߼۸���ɫ����
	private final int FIRST_STAGE = 0;
	private final int SECOND_STAGE = 200;
	private final int THIRD_STAGE = 500;
	private final int FOURTH_STAGE = 1000;
	private final int FIFTH_STAGE = 10000;
	Paint paint;//����
	private int price_up;//�۸�����
	private int price_down;//�۸�����
	private float y_up;//�۸��Ӧ��y�������ޣ�ָ��������ĵ��y����
	private float y_down;//�۸��Ӧ��y�������ޣ�����ָ��������ĵ��y����
	private int bg_height;//���͵ĸ߶�
	private int bg_width;//���͵ĸ߶ȣ���ĸ߶�
	private int span_state;//ÿһ���ȼ�������߶�
	private float scale_h;
	private float btn_x;
	private final int TEXT_PADDING = 15;
	private boolean isUpTouched;
	private boolean isDownTouched;
	//���캯��
	public SlidingButtons(Context context, AttributeSet attrs) {
		super(context, attrs);
		//��ʼ�����ݣ�����ͼƬ
		gray_bg = iBmp(R.drawable.axis_before);
		green_bg = iBmp(R.drawable.axis_after);
		btn = iBmp(R.drawable.btn);
		num_price = iBmp(R.drawable.bg_number);
		paint = new Paint();
		paint.setColor(Color.GRAY);//��ɫ
		price_up = 1000;
		price_down = 200;//�����Զ�������������xml��
	}

	/**
	 * ����ͼƬid����ͼƬ
	 * @param resId
	 * @return
	 */
	public Bitmap iBmp(int resId){
		return BitmapFactory.decodeResource(getResources(), resId);
	}
	
	
	//�����Լ����
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);//������ָ���Ŀ�
		int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
		int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);//������ָ���ĸ�
		int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
		//���Ǹ���wrap_content����£�����ʹ��ϵͳĬ�ϸ����ǿؼ���fill_parentģʽ�����Ǹ����Լ����������������
		//����wrap_contentģʽ�£���ߵ�ȡֵ
		bg_height = gray_bg.getHeight();//���͸߶�
		bg_width = gray_bg.getWidth();//���Ϳ��
		span_state = (bg_height - bg_width)/4;
		int measuredHeight = (modeHeight == MeasureSpec.EXACTLY)?sizeHeight:bg_height;
		measuredHeight = Math.min(measuredHeight,sizeHeight);
		int measuredWidth = 2*measuredHeight/3;
		scale_h = (float)measuredHeight/bg_height;
		setMeasuredDimension(measuredWidth, measuredHeight);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	//�������������
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		//�Ի�����������
		canvas.save();//���浱ǰ����״̬
		canvas.scale(scale_h, scale_h);
		
		//��ʼ�滭
		//���㻬�͵�x����
		int bg_x = (int) ((this.getWidth()/scale_h-gray_bg.getWidth())/2);
		canvas.drawBitmap(gray_bg, bg_x, 0, paint);
		
		//�����ұߵ�5���ı�
		String [] numbers = new String[]{
				"����",
				String.valueOf(FOURTH_STAGE),
				String.valueOf(THIRD_STAGE),
				String.valueOf(SECOND_STAGE),
				String.valueOf(FIRST_STAGE)
		};
		paint.setTextSize(20/scale_h);//�����ı��Ļ��ʳߴ�
		for(int i = 0;i<numbers.length;i++){
			//��ʼ�����ı�
			int text_x = 5*bg_x/4;
			float text_y = (i*span_state+bg_width/2+(paint.descent()-paint.ascent())/2-paint.descent());
			canvas.drawText(numbers[i], text_x, text_y, paint);
		}
		//����������¼۸�Ĵ��
		btn_x = (this.getWidth()/scale_h - btn.getWidth())/2;
		//���޼۸��Ӧ��y����
		y_up = getBtnYByPrice(price_up);
		canvas.drawBitmap(btn, btn_x, y_up-btn.getWidth()/2, paint);
		//���޼۸��Ӧ��y����
		y_down = getBtnYByPrice(price_down);
		canvas.drawBitmap(btn, btn_x, y_down-btn.getWidth()/2, paint);
		
		//����ɫ����
		//�ü����־���
		Rect src = new Rect(0, (int)(y_up+btn.getHeight()/2), bg_width, (int)(y_down-btn.getHeight()/2));
		//���Ƶ���������ϵ�ľ���
		Rect dst = new Rect((int)bg_x, (int)(y_up+btn.getHeight()/2), (int)(bg_x+bg_width), (int)(y_down-btn.getHeight()/2));
		canvas.drawBitmap(green_bg, src, dst, paint);
		//������ߵļ۸񳤷��Σ�ͼƬ�����֣�
		Rect rect_up = getRectByY(y_up);
		canvas.drawBitmap(num_price, null, rect_up, paint);
		Rect rect_down = getRectByY(y_down);
		canvas.drawBitmap(num_price, null, rect_down, paint);
		
		//�����ı�
		float text_up_y = y_up + (paint.descent()-paint.ascent())/2 -paint.descent();
		float text_up_x = (3*rect_up.width()/4 - paint.measureText(String.valueOf(price_up)))/2;
		canvas.drawText(String.valueOf(price_up), text_up_x, text_up_y, paint);
		
		float text_down_y = y_down +(paint.descent()-paint.ascent())/2-paint.descent();
		float text_down_x = (3*rect_down.width()/4 - paint.measureText(String.valueOf(price_down)))/2;
		canvas.drawText(String.valueOf(price_down),text_down_x,text_down_y, paint);
		//��ɻ����Ժ�
		canvas.restore();//����
		super.onDraw(canvas);
		
	}

	private Rect getRectByY(float y) {
		// TODO Auto-generated method stub
		Rect rect = new Rect();
		rect.left = 0;
		rect.right = (int) btn_x;
		float text_h = paint.descent() - paint.ascent();
		rect.top = (int) (y - text_h/2 - TEXT_PADDING);
		rect.bottom = (int) (y+text_h/2 + TEXT_PADDING);
		return rect;
	}

	/**
	 * ���ݼ۸�����۸����ڵ�Y����
	 * @param price_up2
	 * @return
	 */
	private float getBtnYByPrice(int price) {
		float y;
		if(price<0){
			price = 0;
		}
		if(price>10000){
			price = 10000;
		}
		if(price<=FIFTH_STAGE&&price>FOURTH_STAGE){
			y = span_state*(FIFTH_STAGE-price)/(FIFTH_STAGE-FOURTH_STAGE)+bg_width/2;
		}else if(price<=FOURTH_STAGE&&price>THIRD_STAGE){
			y = span_state*(FOURTH_STAGE-price)/(FOURTH_STAGE-THIRD_STAGE)+bg_width/2+span_state;
		}else if(price<=THIRD_STAGE&&price>SECOND_STAGE){
			y = span_state*(THIRD_STAGE-price)/(THIRD_STAGE-SECOND_STAGE)+bg_width/2+2*span_state;
		}else if(price<=SECOND_STAGE&&price>FIRST_STAGE){
			y = span_state*(SECOND_STAGE-price)/(SECOND_STAGE-FIRST_STAGE)+bg_width/2+3*span_state;
		}else{
			y = 4*span_state+bg_width/2;
		}
		return y;
	}
	
	/**
	 * ����y��������ȡ�۸�*Y����ָ�Ļ�����Y����
	 */
	public int getPriceByY(float y){
		int price;
		if(y<bg_width/2){
			y = bg_width/2;
		}
		if(y>(bg_width/2+4*span_state)){
			y = bg_width/2 + 4*span_state;
		}
		if(y>=bg_width/2&&y<bg_width/2+span_state){
			//1000-10000
			price = (int) (FIFTH_STAGE-(FIFTH_STAGE-FOURTH_STAGE)*(y-bg_width/2)/span_state);
		}else if(y>=bg_width/2+span_state&&y<bg_width/2+2*span_state){
			price = (int) (FOURTH_STAGE-(FOURTH_STAGE-THIRD_STAGE)*(y-bg_width/2-span_state)/span_state);
		}else if(y>=bg_width/2+2*span_state&&y<bg_width/2+3*span_state){
			price = (int) (THIRD_STAGE-(THIRD_STAGE-SECOND_STAGE)*(y-bg_width/2-2*span_state)/span_state);
		}else if(y>=bg_width/2+3*span_state&&y<bg_width/2+4*span_state){
			price = (int) (SECOND_STAGE-(SECOND_STAGE-FIRST_STAGE)*(y-bg_width/2-3*span_state)/span_state);
		}else{
			price = 0;
		}
		
		//���ڼ۸���Ҫ���п̶ȵ���С����
		if(price<=1000){
			int mol = price%20;
			if(mol>=10){
				price = price-mol+20;
			}else{
				price = price - mol;
			}
		}
		if(price>1000){
			int mol = price%1000;
			if(mol>=500){
				price = price - mol +1000;
			}else{
				price = price - mol;
			}
		}
		return price;
	}
	
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			float x = event.getX()/scale_h;
			float y = event.getY()/scale_h;
			if(x>=btn_x&&x<=btn_x+btn.getWidth()){
				if(y>=(y_up-btn.getHeight()/2)&&y<=(y_up+btn.getHeight()/2)){
					//˵���������ϱ�
					isUpTouched = true;
					isDownTouched = false;
				}
				if(y>=(y_down-btn.getHeight()/2)&&y<=(y_down+btn.getHeight()/2)){
					//˵���������±�
					isDownTouched = true;
					isUpTouched = false;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			float y2 = event.getY()/scale_h;
			if(isUpTouched){
				price_up = getPriceByY(y2);
				if(price_up<=price_down){
					price_up = price_down;
				}
			}
			
			if(isDownTouched){
				price_down = getPriceByY(y2);
				if(price_down>=price_up){
					price_down = price_up;
				}
			}
			this.invalidate();//ˢ���ػ�
			break;
		case MotionEvent.ACTION_UP:
			isUpTouched = false;
			isDownTouched = false;
			break;
		default:
			break;
		}	
		return true;
	}
}
