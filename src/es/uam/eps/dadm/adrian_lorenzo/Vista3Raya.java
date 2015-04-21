package es.uam.eps.dadm.adrian_lorenzo;

import es.uam.eps.dadm.adrian_lorenzo.logic.Movimiento3Raya;
import es.uam.eps.dadm.adrian_lorenzo.logic.Tablero3Raya;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

@SuppressLint("DrawAllocation")
public class Vista3Raya extends View {

	private Tablero3Raya tablero = null;

	private Paint backgroundPaint;
	private Paint linePaint;
	private static final float STROKE_WIDTH = 3;
	private Paint circlePaintDefault;
	private Paint circlePaintPlayer;
	private ValueAnimator miAnimador;
	private static final int ANIMATION_DURATION = 300;

	private static final float PADDING = 2;
	private float board_width;
	private float board_height;
	private float tile_width;
	private float tile_height;
	private float tile_center;
	private float radioToken;
	private float radioTokenMax;

	public Vista3Raya(Context context, AttributeSet attrs) {

		super(context, attrs);

		circlePaintPlayer = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaintPlayer.setColor(SettingsActivity.getColorPlayer1(context));
		circlePaintDefault = new Paint(Paint.ANTI_ALIAS_FLAG);
		circlePaintDefault.setColor(SettingsActivity.getColorPlayer2(context));

		backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		backgroundPaint.setColor(Color.WHITE);

		linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		linePaint.setColor(this.getResources().getColor(R.color.white_app));
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(STROKE_WIDTH);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int width = this.getWidth();
		int height = this.getHeight();

		// Obtiene la menor medida para dibujar
		// el tablero cuadrado de forma que quepa
		// en la pantalla

		if (width < height)
			height = width;
		else
			width = height;

		// Añade el padding
		board_width = width - PADDING;
		board_height = height - PADDING;

		tile_width = (float) Math.floor(board_width / Tablero3Raya.MAX_SIZE);
		tile_height = (float) Math.floor(board_height / Tablero3Raya.MAX_SIZE);
		radioTokenMax = tile_width / 3 + tile_width / 9;
		radioToken = radioTokenMax;
		tile_center = tile_width / 2;

		// Inicializa la animación de las fichas
		miAnimador = ValueAnimator.ofFloat(0f, radioTokenMax);
		miAnimador.setDuration(ANIMATION_DURATION);
		miAnimador.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				// En cada frame hace más grande el radio, pintando
				// la ficha desde radio 0 a radioTokenMax
				radioToken = (Float) animation.getAnimatedValue();
				invalidate();
			}
		});

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		if (w < h)
			h = w;
		else
			w = h;

		board_width = w - PADDING;
		board_height = h - PADDING;
		tile_center = tile_width / 2;

		tile_width = (float) Math.floor(board_width / Tablero3Raya.MAX_SIZE);
		tile_height = (float) Math.floor(board_height / Tablero3Raya.MAX_SIZE);

		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	protected void onDraw(Canvas canvas) {

		super.onDraw(canvas);

		// Imprime el tablero (las celdas)
		for (float i = 0; i <= Tablero3Raya.MAX_SIZE; i++) {

			canvas.drawLine(PADDING, (i * tile_height) + PADDING, board_width,
					(i * tile_height) + PADDING, linePaint);
			canvas.drawLine((i * tile_width) + PADDING, PADDING,
					(i * tile_width) + PADDING, board_height, linePaint);
		}

		if (this.tablero != null) {

			Movimiento3Raya movimiento = (Movimiento3Raya) tablero
					.getUltimoMovimiento();
			float printRadioToken;
			// Imprime las fichas
			for (int y = 0; y < Tablero3Raya.MAX_SIZE; y++) {
				for (int x = 0; x < Tablero3Raya.MAX_SIZE; x++) {

					if (movimiento != null && movimiento.getFila() == y
							&& movimiento.getColumna() == x)

						printRadioToken = radioToken;

					else
						printRadioToken = radioTokenMax;

					if (tablero.getCelda(x, y) == Tablero3Raya.FICHA_O) {

						canvas.drawCircle((x * tile_width) + tile_center
								+ PADDING, (y * tile_height) + tile_center
								+ PADDING, printRadioToken, circlePaintPlayer);

					}

					else if (tablero.getCelda(x, y) == Tablero3Raya.FICHA_X) {

						canvas.drawCircle((x * tile_width) + tile_center
								+ PADDING, (y * tile_height) + tile_center
								+ PADDING, printRadioToken, circlePaintDefault);

					}
				}
			}
		}
	}

	/**
	 * 
	 * Traduce la coordenada X de la pantalla a la coordenada del juego.
	 * 
	 * @param event
	 * @return
	 */
	public int getXTablero(MotionEvent event) {

		Double x = Math.floor(event.getX() / tile_width);

		return x.intValue();

	}

	/**
	 * 
	 * Traduce la coordenada Y de la pantalla a la coordenada correspondiente
	 * del juego.
	 * 
	 * @param event
	 * @return
	 */
	public int getYTablero(MotionEvent event) {

		Double y = Math.floor(event.getY() / tile_width);
		return y.intValue();

	}

	public void setTablero(Tablero3Raya tablero) {
		this.tablero = tablero;
	}

	/**
	 * 
	 * Actualmente este método sirve unicamente para realizar la animación (de
	 * momento).
	 * 
	 */
	public void notificaCambioVista() {
		if (miAnimador != null)
			miAnimador.start();
	}

}
