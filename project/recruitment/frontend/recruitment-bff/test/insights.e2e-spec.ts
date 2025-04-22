import { Test, TestingModule } from '@nestjs/testing';
import { HttpModule, HttpService } from '@nestjs/axios';
import { of, throwError } from 'rxjs';
import { InsightsController } from '../src/insights/api/insights.controller';
import { InsightsService } from '../src/insights/adapters/services/insights-service/insights.service';
import { AxiosError, AxiosResponseHeaders } from 'axios';
import { InsightsDto } from '../src/insights/api/dto/insights.dto';
import { ServiceInsightsDto } from '../src/insights/adapters/services/insights-service/dto/service-insights.dto';
import { SharedModule } from '../src/shared/shared.module';
import { InsightsBackendNotAvailableException } from '../src/insights/model/exceptions/insights-backend-not-available.exception';
import { HttpException } from '@nestjs/common';

const insightsDto: ServiceInsightsDto = {
  totalPositions: 10,
  averageApplications: 5,
  averageScore: 80.5,
};

let mockGet: jest.Mock;

describe('InsightsController', () => {
  let insightsController: InsightsController;
  mockGet = jest.fn();

  const mockHttpService: Partial<HttpService> = {
    get: mockGet,
  };

  beforeEach(async () => {

    const app: TestingModule = await Test.createTestingModule({
      controllers: [InsightsController],
      imports: [HttpModule, SharedModule],
      providers: [
        InsightsService,
        { provide: HttpService, useValue: mockHttpService }
      ],
    }).compile();

    insightsController = app.get<InsightsController>(InsightsController);

  });

  it('should return insights data', async () => {
    mockGet.mockReturnValue(of({
        data: insightsDto,
        status: 200,
        statusText: 'OK',
        headers: {},
        config: {
          headers: {} as AxiosResponseHeaders,
        },
      }));

    const result = await insightsController.getInsights()
    const expectedResult: InsightsDto = {
      totalPositions: insightsDto.totalPositions,
      averageApplications: insightsDto.averageApplications,
      averageScore: insightsDto.averageScore,
    }

    expect(result).toEqual(expectedResult);
  });

  it('should throw HttpException (status 503) when ECONNREFUSED', async () => {
    const axiosError: Partial<AxiosError> = {
      isAxiosError: true,
      code: 'ECONNREFUSED',
      message: 'Connection refused',
    };

    mockGet.mockReturnValue(throwError(() => axiosError));

    try {
      await insightsController.getInsights();
    } catch (error) {
      expect(error).toBeInstanceOf(HttpException);
      expect(error.status).toEqual(503)
      expect(error.response).toHaveProperty('error');
      expect(error.response).toHaveProperty('message');
    }
  });

  it('should throw generic error for unknown error types', async () => {
    const genericError = new Error('Unexpected failure');

    mockGet.mockImplementation(() => { throw genericError; });

    try {
      await insightsController.getInsights();
    } catch (error) {
      expect(error).toBeInstanceOf(HttpException);
      expect(error.status).toEqual(503)
      expect(error.response).toHaveProperty('error');
      expect(error.response.error).toEqual(2000)
      expect(error.response).toHaveProperty('message');
    }
  });


});
