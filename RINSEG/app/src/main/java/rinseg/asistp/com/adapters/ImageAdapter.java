package rinseg.asistp.com.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rinseg.asistp.com.listener.ListenerClick;
import rinseg.asistp.com.listener.ListenerClickImage;
import rinseg.asistp.com.models.FotoModel;
import rinseg.asistp.com.models.ImagenRO;
import rinseg.asistp.com.models.InspeccionRO;
import rinseg.asistp.com.rinseg.R;
import rinseg.asistp.com.utils.Generic;

/**
 * Created by Carlos Ramos on 30/09/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImagenRO> ListaImagenes;
    private final ListenerClickImage mListener;
    private File mFileAPP;
    private String mCarpetaRop;
    private String mPathImageGallery;
    private Context mContext;

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        //Campos respectivos del item
        public TextView txtComentario;
        public ImageView imageFotoGaleria;
        public final RelativeLayout vLayout;


        public ImageViewHolder(View v) {
            super(v);

            txtComentario = (TextView) v.findViewById(R.id.txt_card_comentario_imagen);
            imageFotoGaleria = (ImageView) v.findViewById(R.id.image_card_foto_galeria);

            vLayout = (RelativeLayout) v.findViewById(R.id.linear_galeria);
        }
    }

    public ImageAdapter(Context pContext, List<ImagenRO> imagenes, File fileAPP, String pathImageGallery, String carpertaRop, ListenerClickImage listener) {
        this.ListaImagenes = imagenes;
        mListener = listener;
        mFileAPP = fileAPP;
        mCarpetaRop = carpertaRop;
        mPathImageGallery = pathImageGallery;
        mContext = pContext;
    }

    @Override
    public int getItemCount() {
        return ListaImagenes.size();
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.cardview_galeria, viewGroup, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder viewHolder, final int i) {
        viewHolder.txtComentario.setText(ListaImagenes.get(i).getDescripcion());
        String nombreImagen = ListaImagenes.get(i).getName();
        //final FotoModel fotoModel = Generic.DevolverImagendeCarpeta(mFileAPP, mPathImageGallery, mCarpetaRop, nombreImagen);
        File folderRop = new File(mFileAPP, mPathImageGallery + mCarpetaRop + "/" + nombreImagen);
        FotoModel fotoModel = new FotoModel();
        fotoModel.uri = Uri.parse(folderRop.getAbsoluteFile().toString());


        if (fotoModel != null) {
            Picasso.with(mContext).load("file://" + fotoModel.uri.getPath())
                    .error(R.drawable.ic_imagen_no_disponible)
                    .placeholder(R.drawable.ic_image)
                    .fit()
                    .centerCrop()
                    .into(viewHolder.imageFotoGaleria);

            // viewHolder.imageFotoGaleria.setImageURI(fotoModel.uri);
        }

        viewHolder.vLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombreImagen = ListaImagenes.get(i).getName();
                File folderRop = new File(mFileAPP, mPathImageGallery + mCarpetaRop + "/" + nombreImagen);
                FotoModel fotoModel = new FotoModel();
                fotoModel.uri = Uri.parse(folderRop.getAbsoluteFile().toString());

                mListener.onItemClicked(viewHolder, i, fotoModel);
            }
        });

    }

}
