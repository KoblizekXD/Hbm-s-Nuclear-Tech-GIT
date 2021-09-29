package com.hbm.render.block.ct;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IIcon;

public class RenderBlocksCT extends RenderBlocks {
	
	public static RenderBlocksCT instance = new RenderBlocksCT();

	VertInfo tl;
	VertInfo tc;
	VertInfo tr;
	VertInfo cl;
	VertInfo cc;
	VertInfo cr;
	VertInfo bl;
	VertInfo bc;
	VertInfo br;
	
	Tessellator tess;
	
	public RenderBlocksCT() {
		super();
		this.tess = Tessellator.instance;
	}
	
	private void initSideInfo() {
		
		if(!this.enableAO)
			return;

		this.tl = new VertInfo(this.colorRedTopLeft, this.colorGreenTopLeft, this.colorBlueTopLeft, this.brightnessTopLeft);
		this.tr = new VertInfo(this.colorRedTopRight, this.colorGreenTopRight, this.colorBlueTopRight, this.brightnessTopRight);
		this.bl = new VertInfo(this.colorRedBottomLeft, this.colorGreenBottomLeft, this.colorBlueBottomLeft, this.brightnessBottomLeft);
		this.br = new VertInfo(this.colorRedBottomRight, this.colorGreenBottomRight, this.colorBlueBottomRight, this.brightnessBottomRight);

		this.tc = VertInfo.avg(tl, tr);
		this.bc = VertInfo.avg(bl, br);
		this.cl = VertInfo.avg(tl, bl);
		this.cr = VertInfo.avg(tr, br);
		
		this.cc = VertInfo.avg(tl, tr, bl, br);
	}

	/*@Override
	public boolean renderStandardBlock(Block block, int x, int y, int z) {
		return super.renderStandardBlock(block, x, y, z);
	}*/

	@Override
	public void renderFaceXPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceXPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceXNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceXNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceYPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceYPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceYNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceYNeg(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZPos(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceZPos(block, x, y, z, icon);
	}

	@Override
	public void renderFaceZNeg(Block block, double x, double y, double z, IIcon icon) {
		super.renderFaceZNeg(block, x, y, z, icon);
	}
	
	private void drawFace(double[] ftl, double[] ftr, double[] fbl, double[] fbr, IIcon itl, IIcon itr, IIcon ibl, IIcon ibr) {

		double[] ftc = avgCoords(ftl, ftr);
		double[] fbc = avgCoords(fbl, fbr);
		double[] fcl = avgCoords(ftl, fbl);
		double[] fcr = avgCoords(ftr, fbr);
		double[] fcc = avgCoords(ftc, fbc);

		drawSubFace(ftl, this.tl, ftc, this.tc, fcl, this.cl, fcc, this.cc, itl);
		drawSubFace(ftc, this.tc, ftr, this.tr, fcc, this.cc, fcr, this.cr, itr);
		drawSubFace(fcl, this.cl, fcc, this.cc, fbl, this.bl, fbc, this.bc, ibl);
		drawSubFace(fcc, this.cc, fcr, this.cr, fbc, this.bc, fbr, this.br, ibr);
	}
	
	private void drawSubFace(double[] ftl, VertInfo ntl, double[] ftr, VertInfo ntr, double[] fbl, VertInfo nbl, double[] fbr, VertInfo nbr, IIcon icon) {
		drawVert(ftl, icon.getMinU(), icon.getMinV(), ntl);
		drawVert(ftr, icon.getMinU(), icon.getMaxV(), ntr);
		drawVert(fbr, icon.getMaxU(), icon.getMaxV(), nbr);
		drawVert(fbl, icon.getMaxU(), icon.getMinV(), nbl);
	}
	
	private void drawVert(double[] coord, double u, double v, VertInfo info) {
		drawVert(coord[0], coord[1], coord[2], u, v, info);
	}
	
	private void drawVert(double x, double y, double z, double u, double v, VertInfo info) {
		
		if(this.enableAO) {
			tess.setColorOpaque_F(info.red, info.blue, info.green);
			tess.setBrightness(info.brightness);
		}
		
		tess.addVertexWithUV(x, y, z, u, v);
	}
	
	private double[] avgCoords(double[] first, double[] second) {
		return new double[] {
				(first[0] + second[0]) / 2,
				(first[1] + second[1]) / 2,
				(first[2] + second[2]) / 2
		};
	}
	
	public static class VertInfo {
		float red;
		float green;
		float blue;
		int brightness;
		
		public VertInfo(float red, float green, float blue, int brightness) {
			this.red = red;
			this.green = green;
			this.blue = blue;
			this.brightness = brightness;
		}
		
		public static VertInfo avg(VertInfo...infos) {
			float r = 0F;
			float g = 0F;
			float b = 0F;
			int l = 0;
			
			for(VertInfo vert : infos) {
				r += vert.red;
				g += vert.green;
				b += vert.blue;
				l += vert.brightness;
			}

			r /= infos.length;
			g /= infos.length;
			b /= infos.length;
			l /= infos.length;
			
			return new VertInfo(r, g, b, l);
		}
	}

	@Override
	public void renderBlockAsItem(Block block, int meta, float mult) {

		block.setBlockBoundsForItemRender();
		this.setRenderBoundsFromBlock(block);
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		tess.startDrawingQuads();
		tess.setNormal(0.0F, -1.0F, 0.0F);
		this.renderFaceYNeg(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 0, meta));
		tess.draw();
		tess.startDrawingQuads();
		tess.setNormal(0.0F, 1.0F, 0.0F);
		this.renderFaceYPos(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 1, meta));
		tess.draw();

		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, -1.0F);
		this.renderFaceZNeg(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 2, meta));
		tess.draw();
		tess.startDrawingQuads();
		tess.setNormal(0.0F, 0.0F, 1.0F);
		this.renderFaceZPos(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 3, meta));
		tess.draw();
		
		tess.startDrawingQuads();
		tess.setNormal(-1.0F, 0.0F, 0.0F);
		this.renderFaceXNeg(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 4, meta));
		tess.draw();
		tess.startDrawingQuads();
		tess.setNormal(1.0F, 0.0F, 0.0F);
		this.renderFaceXPos(block, 0.0D, 0.0D, 0.0D, this.getBlockIconFromSideAndMetadata(block, 5, meta));
		tess.draw();
		
		GL11.glTranslatef(0.5F, 0.5F, 0.5F);
	}
}