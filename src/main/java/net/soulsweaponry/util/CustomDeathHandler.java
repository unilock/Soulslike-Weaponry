package net.soulsweaponry.util;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.soulsweaponry.particles.ParticleHandler;
import org.joml.Matrix4f;

public class CustomDeathHandler {

    private static final float HALF_SQRT_3 = (float)(Math.sqrt(3.0) / 2.0);

    public static void deathExplosionEvent(World world, Vec3d pos, SoundEvent sound, ParticleEffect... particles) {
        if (!world.isClient) {
            ParticleHandler.particleSphereList(world, 1000, pos.getX(), pos.getY(), pos.getZ(), 1f, particles);
        }
        world.playSound(null, BlockPos.ofFloored(pos), sound, SoundCategory.HOSTILE, 1f, 1f);
    }

    public static void renderDeathLight(LivingEntity entity, float entityYaw, float partialTicks, MatrixStack stack, double[] translation,
        VertexConsumerProvider bufferIn, int packedLightIn, int deathTicks, int[] rgbColorOne, int[] rgbColorTwo, int[] rgbColorThree, int[] rgbColorFour) {
        if (deathTicks > 0) {
            float l = ((float)deathTicks + partialTicks) / 200.0f;
            float m = Math.min(l > 0.8f ? (l - 0.8f) / 0.2f : 0.0f, 1.0f);
            Random random = Random.create(432L);
            VertexConsumer vertexConsumer4 = bufferIn.getBuffer(RenderLayer.getLightning());
            stack.push();
            stack.translate(translation[0], translation[1], translation[2]);
            for(int n = 0; (float)n < (l + l * l) / 2.0F * 60.0F; ++n) {
                stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0f));
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0f));
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0f));
                stack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(random.nextFloat() * 360.0f));
                stack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(random.nextFloat() * 360.0f));
                stack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(random.nextFloat() * 360.0f + l * 90.0f));
                float o = random.nextFloat() * 20.0f + 5.0f + m * 10.0f;
                float p = random.nextFloat() * 2.0f + 1.0f + m * 2.0f;
                Matrix4f matrix4f = stack.peek().getPositionMatrix();
                int q = (int)(255.0f * (1.0f - m));
                CustomDeathHandler.renderLight_1(vertexConsumer4, matrix4f, q, rgbColorOne);
                CustomDeathHandler.renderLight_2(vertexConsumer4, matrix4f, o, p, rgbColorTwo);
                CustomDeathHandler.renderLight_3(vertexConsumer4, matrix4f, o, p, rgbColorThree);
                CustomDeathHandler.renderLight_1(vertexConsumer4, matrix4f, q, rgbColorOne);
                CustomDeathHandler.renderLight_3(vertexConsumer4, matrix4f, o, p, rgbColorThree);
                CustomDeathHandler.renderLight_4(vertexConsumer4, matrix4f, o, p, rgbColorFour);
                CustomDeathHandler.renderLight_1(vertexConsumer4, matrix4f, q, rgbColorOne);
                CustomDeathHandler.renderLight_4(vertexConsumer4, matrix4f, o, p, rgbColorFour);
                CustomDeathHandler.renderLight_2(vertexConsumer4, matrix4f, o, p, rgbColorTwo);
            }
            stack.pop();
        }
    }

    private static void renderLight_1(VertexConsumer vertices, Matrix4f matrix, int alpha, int[] rgbColors) {
        vertices.vertex(matrix, 0.0f, 0.0f, 0.0f).color(rgbColors[0], rgbColors[1], rgbColors[2], alpha);
    }

    private static void renderLight_2(VertexConsumer vertices, Matrix4f matrix, float y, float x, int[] rgbColors) {
        vertices.vertex(matrix, -HALF_SQRT_3 * x, y, -0.5f * x).color(rgbColors[0], rgbColors[1], rgbColors[2], 0);
    }

    private static void renderLight_3(VertexConsumer vertices, Matrix4f matrix, float y, float x, int[] rgbColors) {
        vertices.vertex(matrix, HALF_SQRT_3 * x, y, -0.5f * x).color(rgbColors[0], rgbColors[1], rgbColors[2], 0);
    }

    private static void renderLight_4(VertexConsumer vertices, Matrix4f matrix, float y, float z, int[] rgbColors) {
        vertices.vertex(matrix, 0.0f, y, 1.0f * z).color(rgbColors[0], rgbColors[1], rgbColors[2], 0);
    }
}
