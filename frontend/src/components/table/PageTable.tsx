import React from 'react';
import { Table, TableProps } from 'antd';
import type { ColumnsType } from 'antd/es/table';

export interface PageTableProps<T> extends TableProps<T> {
  columns: ColumnsType<T>;
  dataSource?: T[];
  loading?: boolean;
  rowKey?: string | ((record: T) => string);
  pagination?: {
    current?: number;
    pageSize?: number;
    total?: number;
    onChange?: (page: number, pageSize: number) => void;
    showSizeChanger?: boolean;
    showTotal?: (total: number) => React.ReactNode;
  };
}

function PageTable<T extends object>(props: PageTableProps<T>) {
  const { columns, dataSource, loading, rowKey, pagination, ...restProps } = props;

  return (
    <Table<T>
      columns={columns}
      dataSource={dataSource}
      loading={loading}
      rowKey={rowKey}
      pagination={
        pagination
          ? {
              current: pagination.current,
              pageSize: pagination.pageSize,
              total: pagination.total,
              onChange: pagination.onChange,
              showSizeChanger: pagination.showSizeChanger,
              showTotal: pagination.showTotal,
              showQuickJumper: true
            }
          : false
      }
      {...restProps}
    />
  );
}

export default PageTable;