import { Injectable, NotFoundException, ForbiddenException } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Document } from '../entities/document.entity';
import { CreateDocumentDto, UpdateDocumentDto } from './dto/document.dto';

@Injectable()
export class DocumentsService {
  constructor(
    @InjectRepository(Document)
    private documentRepository: Repository<Document>,
  ) {}

  async create(createDocumentDto: CreateDocumentDto, userId: string): Promise<Document> {
    const document = this.documentRepository.create({
      ...createDocumentDto,
      ownerId: userId,
    });
    return this.documentRepository.save(document);
  }

  async findAll(userId: string, role: string): Promise<Document[]> {
    if (role === 'admin') {
      return this.documentRepository.find({
        relations: ['owner'],
        order: { createdAt: 'DESC' },
      });
    }
    return this.documentRepository.find({
      where: { ownerId: userId },
      relations: ['owner'],
      order: { createdAt: 'DESC' },
    });
  }

  async findOne(id: string, userId: string, role: string): Promise<Document> {
    const document = await this.documentRepository.findOne({
      where: { id },
      relations: ['owner'],
    });
    
    if (!document) {
      throw new NotFoundException(`Document with ID ${id} not found`);
    }

    if (document.ownerId !== userId && role !== 'admin') {
      throw new ForbiddenException('You do not have permission to access this document');
    }

    return document;
  }

  async update(id: string, updateDocumentDto: UpdateDocumentDto, userId: string, role: string): Promise<Document> {
    const document = await this.findOne(id, userId, role);
    Object.assign(document, updateDocumentDto);
    return this.documentRepository.save(document);
  }

  async remove(id: string, userId: string, role: string): Promise<void> {
    const document = await this.findOne(id, userId, role);
    await this.documentRepository.remove(document);
  }
}