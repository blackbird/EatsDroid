package com.example.sahil.myapplication;

/**
 * Created by adityaaggarwal on 9/8/16.
 */
/*
 * Copyright 2014-2015 Diego Grancini
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import it.dex.movingimageviewlib.generating.generators.BaseValuesGenerator;
import it.dex.movingimageviewlib.parameters.Parameters;

/**
 * The default ValuesGenerator implementation: it generates:
 * <p/>
 * - X value: it's calculated linearly considering the image can be positioned from -viewWidth to deviceWidth + viewWidth
 * <p/>
 * - Y value: it's calculated linearly considering the image can be positioned from -viewHeight to deviceHeight + viewHeight
 * <p/>
 * - Zoom value: as the default zoom
 * <p/>
 * - Angle value: as the default angle
 * <p/>
 * BaseValuesGenerator created by Diego Grancini on 13/12/2014.
 */
public class CustomValueGenerator extends BaseValuesGenerator {

    public CustomValueGenerator(Parameters parameters) {
        super(parameters);
    }

    @Override
    public float getX(float x) {
        float width = getParameters().getWidth();
        float deviceWidth = getParameters().getDeviceWidth();
        deviceWidth=deviceWidth*2;
        float zoom = getParameters().getZoom();
        float newX = width * x / Parameters.MAX_ANGLE;
        float delta = (deviceWidth - width) / 2 - newX;
        float t = delta * (width / (deviceWidth + width));
        return t * (zoom - 1) / zoom;
    }

    @Override
    public float getY(float y) {
        float height = getParameters().getHeight();
        float deviceHeight = getParameters().getDeviceHeight();
        deviceHeight=deviceHeight*2;
        float zoom = getParameters().getZoom();
        float newY = height * y / Parameters.MAX_ANGLE;
        float delta = (deviceHeight - height) / 2 - newY;
        float t = delta * (height / (deviceHeight + height));
        return t * (zoom - 1) / zoom;
    }
}
